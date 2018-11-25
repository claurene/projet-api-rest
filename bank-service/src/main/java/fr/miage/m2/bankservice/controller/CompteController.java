package fr.miage.m2.bankservice.controller;

import fr.miage.m2.bankservice.model.Carte;
import fr.miage.m2.bankservice.model.Operation;
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

@Controller
@RequestMapping(value = "/comptes", produces = MediaType.APPLICATION_JSON_VALUE)
public class CompteController {

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

    // GET one compte
    @GetMapping(value = "/{compteId}")
    public ResponseEntity<?> getCompte(@PathVariable("compteId") String compteId) {
        return Optional.ofNullable(cr.findById(compteId))
                .filter(Optional::isPresent)
                .map(i -> new ResponseEntity<>(compteToResource(i.get(), true), HttpStatus.OK))
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
        ResponseEntity<?> cartes = carteClient.fetchCartes(compteId);
        return cartes;
    }

    // GET one carte
    @GetMapping(value = "/{compteId}/cartes/{carteId}")
    public ResponseEntity<?> getCarte(@PathVariable("compteId") String compteId, @PathVariable("carteId") String carteId) {
        ResponseEntity<?> carte = carteClient.fetchCarte(compteId,carteId);
        return carte;
        // TODO: fix error page if card doesn't exist (whitelabel error)
    }

    // POST new carte
    @PostMapping(value = "/{compteId}/cartes")
    public ResponseEntity<?> newCarte(@PathVariable("compteId") String compteId, @RequestBody Carte carte) {
        HttpEntity<Carte> test = new HttpEntity<>(carte);
        ResponseEntity<?> res = carteClient.postCarte(compteId,test);
        return res;
    }

    // PUT update carte
    @PutMapping(value="/{compteId}/cartes/{carteId}")
    public ResponseEntity<?> putCarte(@RequestBody Carte carte, @PathVariable("compteId") String compteId, @PathVariable("carteId") String id) {
        /*if (!body.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // N'est pas créé si il n'existe pas
        }
        if (!cr.existsByIdAndCompteid(id,compteId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }*/
        //TODO
        HttpEntity<Carte> test = new HttpEntity<>(carte);
        ResponseEntity<?> res = carteClient.putCarte(compteId,id,test);
        return res;
    }

    // DELETE carte
    @DeleteMapping(value="/{compteId}/cartes/{carteId}")
    public ResponseEntity<?> deleteCarte(@PathVariable("compteId") String compteId, @PathVariable("carteId") String id) {
        // TODO
        ResponseEntity<?> res = carteClient.deleteCarte(compteId,id);
        return res;
    }

    ////// Operations //////

    // GET operations (?categorie,commercant,pays)
    @GetMapping(value = "/{compteId}/operations")
    public ResponseEntity<?> getAllOperations(@PathVariable("compteId") String compteId,
                                              @RequestParam("categorie") Optional<String> categorie,
                                              @RequestParam("commercant") Optional<String> commercant,
                                              @RequestParam("pays") Optional<String> pays ) {
        ResponseEntity<?> operations = operationClient.fetchOperations(compteId,categorie,commercant,pays);
        return operations;
    }

    // GET one operation
    @GetMapping(value = "/{compteId}/operations/{operationId}")
    public ResponseEntity<?> getOperation(@PathVariable("compteId") String compteId, @PathVariable("operationId") String operationId) {
        ResponseEntity<?> operation = operationClient.fetchOperation(compteId,operationId);
        return operation;
        // TODO: fix error page if operation doesn't exist (whitelabel error)
    }

    // POST new operation
    @PostMapping(value = "/{compteId}/operations")
    public ResponseEntity<?> newOperation(@PathVariable("compteId") String compteId, @RequestBody Operation operation) {
        //TODO: taux
        Compte c1 = cr.findById(compteId).get(); //TODO: getForObjet + if present
        if (c1.getPays().equals(operation.getPays())) {
            // Pas de taux
            operation.setTaux(new BigDecimal(0));
        } else {
            // TODO: vérif + clean + secure
            String source = pdr.findByPays(operation.getPays()).get().getDevise();
            String cible = pdr.findByPays(c1.getPays()).get().getDevise();
            operation.setTaux(bourseClient.getValeurDeChange(source,cible));
            operation.setMontant(Float.parseFloat(bourseClient.getMontant(source,cible,BigDecimal.valueOf(operation.getMontant())).toString()));
        }
        HttpEntity<Operation> test = new HttpEntity<>(operation);
        ResponseEntity<?> res = operationClient.postOperation(compteId,test);
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
        Compte c1 = cr.findById(compteId).get(); //TODO: getForObjet + if present
        Compte c2 = cr.findByIban(payload.get("IBAN")).get(); //TODO check both conditions
        // TODO: check + secure
        // TODO: taux
        Operation o1 = new Operation(
                "dummy",
                payload.get("dateheure"),
                "transfert",
                Float.parseFloat(payload.get("montant"))*-1,
                payload.get("IBAN"),
                "transfert",
                c1.getPays(),
                new BigDecimal(100),
                "dummy"
        );
        ResponseEntity<?> res1 = operationClient.postOperation(compteId,new HttpEntity<>(o1));
        Operation o2 = new Operation(
                "dummy",
                payload.get("dateheure"),
                "transfert",
                Float.parseFloat(payload.get("montant")),
                c1.getIban(),
                "transfert",
                c1.getPays(),
                new BigDecimal(100),
                "dummy"
        );
        ResponseEntity<?> res2 = operationClient.postOperation(c2.getId(),new HttpEntity<>(o2));
        // TODO: send correct answer
        return new ResponseEntity<>(HttpStatus.OK);
    }

    ///// Private /////

    // Méthodes "ToRessource"
    private Resource<Compte> compteToResource(Compte compte, Boolean collection) {
        Link selfLink = linkTo(CompteRepository.class)
                .slash(compte.getId())
                .withSelfRel();
        // TODO implement getAllComptes
        /*if (collection) {
            Link collectionLink = linkTo(methodOn(CompteController.class).getAllComptes()).withSelfRel();
            return new Resource<>(compte, selfLink, collectionLink);
        } else {
            return new Resource<>(compte, selfLink);
        }*/
        return new Resource<>(compte,selfLink);
    }
}
