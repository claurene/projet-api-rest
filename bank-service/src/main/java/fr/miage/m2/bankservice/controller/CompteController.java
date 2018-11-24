package fr.miage.m2.bankservice.controller;

import fr.miage.m2.bankservice.model.Carte;
import fr.miage.m2.bankservice.model.Operation;
import fr.miage.m2.bankservice.proxy.CarteClient;
import fr.miage.m2.bankservice.model.Compte;
import fr.miage.m2.bankservice.proxy.OperationClient;
import fr.miage.m2.bankservice.repository.CompteRepository;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/comptes", produces = MediaType.APPLICATION_JSON_VALUE)
public class CompteController {

    private final CompteRepository cr;

    private final CarteClient carteClient;

    private final OperationClient operationClient;

    public CompteController(CompteRepository cr, CarteClient carteClient, OperationClient operationClient) {
        this.cr = cr;
        this.carteClient = carteClient;
        this.operationClient = operationClient;
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

    // GET operations
    @GetMapping(value = "/{compteId}/operations")
    public ResponseEntity<?> getAllOperations(@PathVariable("compteId") String compteId) {
        ResponseEntity<?> operations = operationClient.fetchOperations(compteId);
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
        HttpEntity<Operation> test = new HttpEntity<>(operation);
        ResponseEntity<?> res = operationClient.postOperation(compteId,test);
        return res;
    }

    // GET solde
    @GetMapping(value = "/{compteId}/solde")
    public ResponseEntity<?> getSolde(@PathVariable("compteId") String compteId) {
        return operationClient.getSolde(compteId);
    }

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
