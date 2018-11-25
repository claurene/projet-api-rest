package fr.miage.m2.bankservice.controller;

import fr.miage.m2.bankservice.model.Carte;
import fr.miage.m2.bankservice.model.Operation;
import fr.miage.m2.bankservice.model.PaysDevise;
import fr.miage.m2.bankservice.proxy.BourseClient;
import fr.miage.m2.bankservice.proxy.CarteClient;
import fr.miage.m2.bankservice.model.Compte;
import fr.miage.m2.bankservice.proxy.OperationClient;
import fr.miage.m2.bankservice.repository.CompteRepository;
import fr.miage.m2.bankservice.repository.PaysDeviseRepository;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/comptes", produces = MediaType.APPLICATION_JSON_VALUE)
public class CompteController {

    ///// Initialisation /////

    private final CompteRepository cr;
    private final PaysDeviseRepository pdr;

    private final CarteClient carteClient;
    private final OperationClient operationClient;
    private final BourseClient bourseClient;

    public CompteController(CompteRepository cr, PaysDeviseRepository pdr, CarteClient carteClient, OperationClient operationClient, BourseClient bourseClient) {
        this.cr = cr;
        this.pdr = pdr;
        this.carteClient = carteClient;
        this.operationClient = operationClient;
        this.bourseClient = bourseClient;
    }

    ///// Comptes /////

    // GET one compte
    @GetMapping(value = "/{compteId}")
    public ResponseEntity<?> getCompte(@PathVariable("compteId") String compteId) {
        return Optional.ofNullable(cr.findById(compteId))
                .filter(Optional::isPresent)
                .map(i -> new ResponseEntity<>(compteToResource(i.get()), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // POST compte
    @PostMapping
    public ResponseEntity<?> newCompte(@RequestBody Compte compte) {
        compte.setId(UUID.randomUUID().toString()); // Donne un nouvel identifiant
        Compte saved = cr.save(compte); // Fait persister l'carte
        HttpHeaders responseHeader = new HttpHeaders(); // Génère un nouveau header pour la réponse
        responseHeader.setLocation(linkTo(CompteController.class).slash(saved.getId()).toUri()); // La localisation (URI) de l'carte est un lien vers sa classe, ajoute un '/' et renvoi l'identifiant (cartes/123abc...)
        return new ResponseEntity<>(null, responseHeader, HttpStatus.CREATED);
    }
    
    ////// Cartes //////

    // GET cartes
    @GetMapping(value = "/{compteId}/cartes")
    public ResponseEntity<?> getAllCartes(@PathVariable("compteId") String compteId) {
        return carteClient.fetchCartes(compteId);
    }

    // GET one carte
    @GetMapping(value = "/{compteId}/cartes/{carteId}")
    public ResponseEntity<?> getCarte(@PathVariable("compteId") String compteId, @PathVariable("carteId") String carteId) {
        return carteClient.fetchCarte(compteId,carteId);
    }

    // POST new carte
    @PostMapping(value = "/{compteId}/cartes")
    public ResponseEntity<?> newCarte(@PathVariable("compteId") String compteId, @RequestBody Carte carte) {
        return carteClient.postCarte(compteId,new HttpEntity<>(carte));
    }

    // PUT update carte
    @PutMapping(value="/{compteId}/cartes/{carteId}")
    public ResponseEntity<?> putCarte(@RequestBody Carte carte, @PathVariable("compteId") String compteId, @PathVariable("carteId") String id) {
        return carteClient.putCarte(compteId,id,new HttpEntity<>(carte));
    }

    // DELETE carte
    @DeleteMapping(value="/{compteId}/cartes/{carteId}")
    public ResponseEntity<?> deleteCarte(@PathVariable("compteId") String compteId, @PathVariable("carteId") String id) {
        return carteClient.deleteCarte(compteId,id);
    }

    ////// Operations //////

    // GET operations (?categorie,commercant,pays)
    @GetMapping(value = "/{compteId}/operations")
    public ResponseEntity<?> getAllOperations(@PathVariable("compteId") String compteId,
                                              @RequestParam("categorie") Optional<String> categorie,
                                              @RequestParam("commercant") Optional<String> commercant,
                                              @RequestParam("pays") Optional<String> pays ) {
        return operationClient.fetchOperations(compteId,categorie,commercant,pays);
    }

    // GET one operation
    @GetMapping(value = "/{compteId}/operations/{operationId}")
    public ResponseEntity<?> getOperation(@PathVariable("compteId") String compteId, @PathVariable("operationId") String operationId) {
        return operationClient.fetchOperation(compteId,operationId);
    }

    // POST new operation
    @PostMapping(value = "/{compteId}/operations")
    public ResponseEntity<?> newOperation(@PathVariable("compteId") String compteId, @RequestBody Operation operation) {
        ResponseEntity<?> res;

        Optional<Compte> opt = cr.findById(compteId);
        if (opt.isPresent()) {
            Compte c1 = opt.get();
            // TODO: try catch, send 500 if not available ?
            // TODO: send illegal argument exception / BAD REQUEST if pays not found ?
            operation.setTaux(this.getTaux(operation.getPays(),c1.getPays()));
            BigDecimal f =  this.getMontant(operation.getPays(),c1.getPays(),BigDecimal.valueOf(operation.getMontant()));
            operation.setMontant(Float.valueOf(f.toString())); // TODO: change montant type ?
            res = operationClient.postOperation(compteId,new HttpEntity<>(operation));
        } else {
            // TODO: 404
            res = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return res;
    }

    // GET solde
    @GetMapping(value = "/{compteId}/solde")
    public ResponseEntity<?> getSolde(@PathVariable("compteId") String compteId) {
        return operationClient.getSolde(compteId);
    }

    // POST transfert
    @PostMapping(value = "/{compteId}/transfert")
    public ResponseEntity<?> newTransfert(@PathVariable("compteId") String compteId, @RequestBody Map<String,String> payload) {
        Optional<Compte> opt1 = cr.findById(compteId);
        Optional<Compte> opt2 = cr.findByIban(payload.get("IBAN"));
        if (opt1.isPresent() && opt2.isPresent()) {
            Compte c1 = opt1.get();
            Compte c2 = opt2.get();
            // TODO: check operations avec if http reponse = created then...
            Operation o1 = new Operation(
                    "dummy",
                    payload.get("dateheure"),
                    "Transfert vers "+c2.getIban(),
                    Float.parseFloat(payload.get("montant"))*-1,
                    payload.get("IBAN"),
                    "", // TODO: ?
                    c1.getPays(),
                    new BigDecimal(0),
                    "dummy"
            );
            ResponseEntity<?> res1 = operationClient.postOperation(compteId,new HttpEntity<>(o1));
            Operation o2 = new Operation(
                    "dummy",
                    payload.get("dateheure"),
                    "Transfert de "+c1.getIban(),
                    Float.parseFloat(String.valueOf(this.getMontant(c1.getPays(),c2.getPays(),new BigDecimal(payload.get("montant"))))), //TODO: à changer...
                    c1.getIban(),
                    "",
                    c1.getPays(),
                    this.getTaux(c1.getPays(),c2.getPays()),
                    "dummy"
            );
            ResponseEntity<?> res2 = operationClient.postOperation(c2.getId(),new HttpEntity<>(o2));
        } else {
            // TODO: 404
        }
        // TODO: send correct answer
        return new ResponseEntity<>(HttpStatus.OK);
    }

    ///// Private /////

    // Méthodes "ToRessource"
    private Resource<Compte> compteToResource(Compte compte) {
        Link selfLink = linkTo(CompteRepository.class)
                .slash(compte.getId())
                .withSelfRel();
        Resource res = new Resource<>(compte,selfLink);
        res.add(linkTo(methodOn(CompteController.class).getAllCartes(compte.getId())).withRel("cartes"));
        res.add(linkTo(methodOn(CompteController.class).getAllOperations(compte.getId(),null,null,null)).withRel("operations"));
        res.add(linkTo(methodOn(CompteController.class).getSolde(compte.getId())).withRel("solde"));
        res.add(linkTo(methodOn(CompteController.class).newTransfert(compte.getId(),null)).withRel("transfert")); // TODO: relevant ??
        return res;
    }

    private BigDecimal getTaux(String pays1, String pays2){
        BigDecimal taux = new BigDecimal(0);
        Optional<PaysDevise> opt1 = pdr.findByPays(pays1);
        Optional<PaysDevise> opt2 = pdr.findByPays(pays2);
        if (opt1.isPresent() && opt2.isPresent()) {
            String source = opt1.get().getDevise();
            String cible = opt2.get().getDevise();
            taux = bourseClient.getValeurDeChange(source, cible);
        } else { // TODO: else throws pays not found exception ?
            System.out.println("ATTENTION: pays not found !");
        }
        return taux;
    }

    private BigDecimal getMontant(String pays1, String pays2, BigDecimal qte){
        BigDecimal montant = qte;
        Optional<PaysDevise> opt1 = pdr.findByPays(pays1);
        Optional<PaysDevise> opt2 = pdr.findByPays(pays2);
        if (opt1.isPresent() && opt2.isPresent()) {
            String source = opt1.get().getDevise();
            String cible = opt2.get().getDevise();
            montant = bourseClient.getMontant(source, cible, qte);
        } else { // TODO: else throws pays not found exception ?
            System.out.println("ATTENTION: pays not found !");
        }
        return montant;
    }
}
