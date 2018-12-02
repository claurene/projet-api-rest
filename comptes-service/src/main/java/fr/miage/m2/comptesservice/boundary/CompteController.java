package fr.miage.m2.comptesservice.boundary;

import fr.miage.m2.comptesservice.entity.Compte;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@Controller
@RequestMapping(value = "/comptes", produces = MediaType.APPLICATION_JSON_VALUE)
public class CompteController {

    ///// Initialisation /////

    private final CompteRepository cr;

    public CompteController(CompteRepository cr) {
        this.cr = cr;
    }

    ///// Comptes /////

    // GET one compte
    @GetMapping(value = "/{compteId}")
    public ResponseEntity<?> getCompte(@PathVariable("compteId") String compteId) {
        return Optional.ofNullable(cr.findById(compteId))
                .filter(Optional::isPresent)
                .map(i -> new ResponseEntity<>(new Resource<>(i.get()), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // GET compte ID by IBAN
    // Problèmes de routing si uniquement objet renvoyé (Spring ne peut pas exposer l'id)
    @GetMapping(value = "/iban/{iban}")
    public ResponseEntity<String> getCompteIdByIban(@PathVariable("iban") String iban) {
        return Optional.ofNullable(cr.findByIban(iban))
                .filter(Optional::isPresent)
                .map(i -> new ResponseEntity<>(i.get().getId(), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // POST compte
    @PostMapping
    public ResponseEntity<?> newCompte(@RequestBody Compte compte) {
        //compte.setId(UUID.randomUUID().toString());
        Compte saved = cr.save(compte); // Fait persister le compte
        HttpHeaders responseHeader = new HttpHeaders(); // Génère un nouveau header pour la réponse
        responseHeader.setLocation(linkTo(methodOn(CompteController.class).getCompte(saved.getId())).toUri());
        return new ResponseEntity<>(null, responseHeader, HttpStatus.CREATED);
    }

}
