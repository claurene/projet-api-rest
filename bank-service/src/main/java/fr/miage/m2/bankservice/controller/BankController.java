package fr.miage.m2.bankservice.controller;

import com.netflix.discovery.EurekaClient;
import fr.miage.m2.bankservice.exception.CountryNotFoundException;
import fr.miage.m2.bankservice.exception.DeviseNotFoundException;
import fr.miage.m2.bankservice.model.Carte;
import fr.miage.m2.bankservice.model.Operation;
import fr.miage.m2.bankservice.model.PaysDevise;
import fr.miage.m2.bankservice.proxy.BourseClient;
import fr.miage.m2.bankservice.proxy.CarteClient;
import fr.miage.m2.bankservice.model.Compte;
import fr.miage.m2.bankservice.proxy.CompteClient;
import fr.miage.m2.bankservice.proxy.OperationClient;
import fr.miage.m2.bankservice.repository.PaysDeviseRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class BankController {

    ///// Initialisation /////

    private final PaysDeviseRepository pdr;

    private final CarteClient carteClient;
    private final CompteClient compteClient;
    private final OperationClient operationClient;
    private final BourseClient bourseClient;

    public BankController(PaysDeviseRepository pdr, CarteClient carteClient, CompteClient compteClient, OperationClient operationClient, BourseClient bourseClient) {
        this.pdr = pdr;
        this.carteClient = carteClient;
        this.compteClient = compteClient;
        this.operationClient = operationClient;
        this.bourseClient = bourseClient;
    }

    ///// Comptes /////

    // GET one compte // TODO GET BY iban instead of ID ?
    @GetMapping(value = "/comptes/{compteId}")
    public ResponseEntity<?> getCompte(@PathVariable("compteId") String compteId) {
        return compteClient.fetchCompte(compteId);
    }

    // POST compte
    @PostMapping
    public ResponseEntity<?> newCompte(@RequestBody Compte compte) {
        return compteClient.postCompte(new HttpEntity<>(compte));
    }
    
    ////// Cartes //////

    // GET cartes
    @GetMapping(value = "/comptes/{compteId}/cartes")
    public ResponseEntity<?> getAllCartes(@PathVariable("compteId") String compteId) {
        return carteClient.fetchCartes(compteId);
    }

    // GET one carte // TODO GET BY numero instead of ID ?
    @GetMapping(value = "/comptes/{compteId}/cartes/{carteId}")
    public ResponseEntity<?> getCarte(@PathVariable("compteId") String compteId, @PathVariable("carteId") String carteId) {
        return carteClient.fetchCarte(compteId,carteId);
    }

    // POST new carte
    @PostMapping(value = "/comptes/{compteId}/cartes")
    public ResponseEntity<?> newCarte(@PathVariable("compteId") String compteId, @RequestBody Carte carte) {
        return carteClient.postCarte(compteId,new HttpEntity<>(carte));
    }

    // PUT update carte
    @PutMapping(value="/comptes/{compteId}/cartes/{carteId}")
    public ResponseEntity<?> putCarte(@RequestBody Carte carte, @PathVariable("compteId") String compteId, @PathVariable("carteId") String id) {
        return carteClient.putCarte(compteId,id,new HttpEntity<>(carte));
    }

    // DELETE carte
    @DeleteMapping(value="/comptes/{compteId}/cartes/{carteId}")
    public ResponseEntity<?> deleteCarte(@PathVariable("compteId") String compteId, @PathVariable("carteId") String id) {
        return carteClient.deleteCarte(compteId,id);
    }

    ////// Operations //////

    // GET operations (?categorie,commercant,pays)
    @GetMapping(value = "/comptes/{compteId}/operations")
    public ResponseEntity<?> getAllOperations(@PathVariable("compteId") String compteId,
                                              @RequestParam("categorie") Optional<String> categorie,
                                              @RequestParam("commercant") Optional<String> commercant,
                                              @RequestParam("pays") Optional<String> pays ) {
        return operationClient.fetchOperations(compteId,categorie,commercant,pays);
    }

    // GET one operation
    @GetMapping(value = "/comptes/{compteId}/operations/{operationId}")
    public ResponseEntity<?> getOperation(@PathVariable("compteId") String compteId, @PathVariable("operationId") String operationId) {
        return operationClient.fetchOperation(compteId,operationId);
    }

    // POST new operation
    @PostMapping(value = "/comptes/{compteId}/operations")
    public ResponseEntity<?> newOperation(@PathVariable("compteId") String compteId, @RequestBody Operation operation) {
        // Note : empeche opération de fonctionner si le service compte n'est pas disponible
        try {
            Compte c1 = compteClient.getCompteAsObject(compteId);
            try {
                operation.setTaux(this.getTaux(operation.getPays(),c1.getPays()));
                BigDecimal f =  this.getMontant(operation.getPays(),c1.getPays(),operation.getMontant());
                operation.setMontant(new BigDecimal(f.toString()));

                return operationClient.postOperation(compteId,new HttpEntity<>(operation));
            } catch (CountryNotFoundException | DeviseNotFoundException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (HttpClientErrorException e) {
            // Gère le 404 et le 500 (compte not found / service unavailable)
            return new ResponseEntity<>(e.getStatusCode());
        }
    }

    // GET solde
    @GetMapping(value = "/comptes/{compteId}/solde")
    public ResponseEntity<?> getSolde(@PathVariable("compteId") String compteId) {
        return operationClient.getSolde(compteId);
    }

    // POST transfert
    @PostMapping(value = "/comptes/{compteId}/transfert")
    public ResponseEntity<?> newTransfert(@PathVariable("compteId") String compteId, @RequestBody Map<String,String> payload) {
        try {
            Compte c1 = compteClient.getCompteAsObject(compteId);
            String id2 = compteClient.getCompteIdByIban(payload.get("IBAN"));
            Compte c2 = compteClient.getCompteAsObject(id2);
            // Préparation des données à envoyer
            Operation o1 = new Operation(
                    "dummy",
                    payload.get("dateheure"),
                    "Transfert vers "+c2.getIban(),
                    new BigDecimal(Float.parseFloat(payload.get("montant"))*-1),
                    payload.get("IBAN"),
                    "", // TODO: ?
                    c1.getPays(),
                    new BigDecimal(1),
                    "dummy"
            );
            try {
                Operation o2 = new Operation(
                        "dummy2",
                        payload.get("dateheure"),
                        "Transfert de "+c1.getIban(),
                        this.getMontant(c1.getPays(),c2.getPays(),new BigDecimal(payload.get("montant"))),
                        c1.getIban(),
                        "",
                        c1.getPays(),
                        this.getTaux(c1.getPays(),c2.getPays()),
                        "dummy"
                );
                // Création des opérations
                ResponseEntity<?> res1 = operationClient.postOperation(compteId,new HttpEntity<>(o1));
                if (res1.getStatusCode().equals(HttpStatus.CREATED)) {
                    ResponseEntity<?> res2 = operationClient.postOperation(id2,new HttpEntity<>(o2));
                    // TODO: better check ?
                    return new ResponseEntity<>(res2.getStatusCode());
                } else {
                    return new ResponseEntity<>(res1.getStatusCode());
                }
                // TODO: headers id location ??
            } catch (CountryNotFoundException | DeviseNotFoundException e) {
                e.printStackTrace(); //TODO: msg d'erreur ?
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }
    }

    ///// Private /////

    private BigDecimal getTaux(String pays1, String pays2) throws CountryNotFoundException, DeviseNotFoundException {
        BigDecimal taux = new BigDecimal(1);
        if (!pays1.equals(pays2)) {
            Optional<PaysDevise> opt1 = pdr.findByPays(pays1);
            Optional<PaysDevise> opt2 = pdr.findByPays(pays2);
            if (opt1.isPresent() && opt2.isPresent()) {
                String source = opt1.get().getDevise();
                String cible = opt2.get().getDevise();
                taux = bourseClient.getValeurDeChange(source, cible);
                if (taux == null) {
                    throw new DeviseNotFoundException();
                }
            } else {
                throw new CountryNotFoundException();
            }
        }
        return taux;
    }

    private BigDecimal getMontant(String pays1, String pays2, BigDecimal qte) throws CountryNotFoundException, DeviseNotFoundException {
        BigDecimal montant = qte;
        if (!pays1.equals(pays2)) {
            Optional<PaysDevise> opt1 = pdr.findByPays(pays1);
            Optional<PaysDevise> opt2 = pdr.findByPays(pays2);
            if (opt1.isPresent() && opt2.isPresent()) {
                String source = opt1.get().getDevise();
                String cible = opt2.get().getDevise();
                montant = bourseClient.getMontant(source, cible, qte);
                if (montant == null) {
                    throw new DeviseNotFoundException();
                }
            } else {
                throw new CountryNotFoundException();
            }
        }
        return montant;
    }
}
