package fr.miage.m2.bankservice.controller;

import fr.miage.m2.bankservice.exception.CountryNotFoundException;
import fr.miage.m2.bankservice.exception.DeviseNotFoundException;
import fr.miage.m2.bankservice.model.*;
import fr.miage.m2.bankservice.proxy.*;
import fr.miage.m2.bankservice.repository.PaysDeviseRepository;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class BankController {

    ///// Initialisation /////

    private final PaysDeviseRepository pdr;

    private final CarteClient carteClient;
    private final CompteClient compteClient;
    private final OperationClient operationClient;
    private final BourseClient bourseClient;
    private final AuthClient authClient;

    public BankController(PaysDeviseRepository pdr, CarteClient carteClient, CompteClient compteClient, OperationClient operationClient, BourseClient bourseClient, AuthClient authClient) {
        this.pdr = pdr;
        this.carteClient = carteClient;
        this.compteClient = compteClient;
        this.operationClient = operationClient;
        this.bourseClient = bourseClient;
        this.authClient = authClient;
    }

    @RequestMapping("/swagger")
    public String swagger(){
        return "redirect:/swagger-ui.html";
    }

    ///// Security /////

    @PostMapping(value = "/sign-up")
    public ResponseEntity<?> signUp(@RequestBody User user){
        return this.authClient.signUp(new HttpEntity<>(user));
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody User user){
        return this.authClient.login(new HttpEntity<>(user));
    }

    ///// Comptes /////

    // GET one compte // TODO GET BY iban instead of ID ?
    @GetMapping(value = "/comptes/{compteId}")
    public ResponseEntity<?> getCompte(@PathVariable("compteId") String compteId) {
        if (checkCompteId(compteId)) {
            return compteClient.fetchCompte(compteId);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // POST compte
    @PostMapping(value = "/comptes")
    public ResponseEntity<?> newCompte(@RequestBody Compte compte) {
        // SET ID according to bearer token
        compte.setId(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        return compteClient.postCompte(new HttpEntity<>(compte));
    }
    
    ////// Cartes //////

    // GET cartes
    @GetMapping(value = "/comptes/{compteId}/cartes")
    public ResponseEntity<?> getAllCartes(@PathVariable("compteId") String compteId) {
        if (checkCompteId(compteId)) {
            return carteClient.fetchCartes(compteId);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // GET one carte // TODO GET BY numero instead of ID ?
    @GetMapping(value = "/comptes/{compteId}/cartes/{carteId}")
    public ResponseEntity<?> getCarte(@PathVariable("compteId") String compteId, @PathVariable("carteId") String carteId) {
        if (checkCompteId(compteId)) {
            return carteClient.fetchCarte(compteId,carteId);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // POST new carte
    @PostMapping(value = "/comptes/{compteId}/cartes")
    public ResponseEntity<?> newCarte(@PathVariable("compteId") String compteId, @RequestBody Carte carte) {
        if (checkCompteId(compteId)) {
            return carteClient.postCarte(compteId,new HttpEntity<>(carte));
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // PUT update carte
    @PutMapping(value="/comptes/{compteId}/cartes/{carteId}")
    public ResponseEntity<?> putCarte(@RequestBody Carte carte, @PathVariable("compteId") String compteId, @PathVariable("carteId") String id) {
        if (checkCompteId(compteId)) {
            return carteClient.putCarte(compteId,id,new HttpEntity<>(carte));
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // DELETE carte
    @DeleteMapping(value="/comptes/{compteId}/cartes/{carteId}")
    public ResponseEntity<?> deleteCarte(@PathVariable("compteId") String compteId, @PathVariable("carteId") String id) {
        if (checkCompteId(compteId)) {
            return carteClient.deleteCarte(compteId,id);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    ////// Operations //////

    // GET operations (?categorie,commercant,pays)
    @GetMapping(value = "/comptes/{compteId}/operations")
    public ResponseEntity<?> getAllOperations(@PathVariable("compteId") String compteId,
                                              @RequestParam("categorie") Optional<String> categorie,
                                              @RequestParam("commercant") Optional<String> commercant,
                                              @RequestParam("pays") Optional<String> pays ) {
        if (checkCompteId(compteId)) {
            return operationClient.fetchOperations(compteId,categorie,commercant,pays);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // GET one operation
    @GetMapping(value = "/comptes/{compteId}/operations/{operationId}")
    public ResponseEntity<?> getOperation(@PathVariable("compteId") String compteId, @PathVariable("operationId") String operationId) {
        if (checkCompteId(compteId)) {
            return operationClient.fetchOperation(compteId,operationId);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // POST new operation
    @PostMapping(value = "/comptes/{compteId}/operations")
    public ResponseEntity<?> newOperation(@PathVariable("compteId") String compteId, @RequestBody Operation operation) {
        if (checkCompteId(compteId)) {
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
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // GET solde
    @GetMapping(value = "/comptes/{compteId}/solde")
    public ResponseEntity<?> getSolde(@PathVariable("compteId") String compteId) {
        if (checkCompteId(compteId)) {
            return operationClient.getSolde(compteId);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // POST transfert
    @PostMapping(value = "/comptes/{compteId}/transfert")
    public ResponseEntity<?> newTransfert(@PathVariable("compteId") String compteId, @RequestBody Transfert payload) {
        if (checkCompteId(compteId)) {
            try {
                Compte c1 = compteClient.getCompteAsObject(compteId);
                String id2 = compteClient.getCompteIdByIban(payload.getIBAN());
                Compte c2 = compteClient.getCompteAsObject(id2);
                // Préparation des données à envoyer
                Operation o1 = new Operation(
                        "dummy",
                        payload.getDateheure(),
                        "Transfert vers "+c2.getIban(),
                        new BigDecimal(Float.parseFloat(payload.getMontant())*-1),
                        payload.getIBAN(),
                        "", // TODO: ?
                        c1.getPays(),
                        new BigDecimal(1),
                        "dummy"
                );
                try {
                    Operation o2 = new Operation(
                            "dummy2",
                            payload.getDateheure(),
                            "Transfert de "+c1.getIban(),
                            this.getMontant(c1.getPays(),c2.getPays(),new BigDecimal(payload.getMontant())),
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
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    ///// Private /////

    ///// Conversion devise /////

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

    ///// Security /////

    private boolean checkCompteId(String compteId) {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(compteId);
    }
}
