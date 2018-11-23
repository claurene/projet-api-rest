package fr.miage.m2.bankservice.controller;

import fr.miage.m2.bankservice.model.Carte;
import fr.miage.m2.bankservice.proxy.CarteClient;
import fr.miage.m2.bankservice.model.Compte;
import fr.miage.m2.bankservice.repository.CompteRepository;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/comptes", produces = MediaType.APPLICATION_JSON_VALUE)
public class CompteController {

    private final CompteRepository cr;

    private final CarteClient carteClient;

    public CompteController(CompteRepository cr, CarteClient carteClient) {
        this.cr = cr;
        this.carteClient = carteClient;
    }

    // GET one
    @GetMapping(value = "/{compteId}")
    public ResponseEntity<?> getCompte(@PathVariable("compteId") Long compteId) {
        return Optional.ofNullable(cr.findById(compteId))
                .filter(Optional::isPresent)
                .map(i -> new ResponseEntity<>(compteToResource(i.get(), true), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // GET cartes
    @GetMapping(value = "/{compteId}/cartes")
    public ResponseEntity<?> getAllCartes(@PathVariable("compteId") Long compteId) {
        ResponseEntity<?> cartes = carteClient.fetchCartes(compteId.toString());
        return cartes;
    }

    // GET one carte
    @GetMapping(value = "/{compteId}/cartes/{carteId}")
    public ResponseEntity<?> getCarte(@PathVariable("compteId") Long compteId, @PathVariable("carteId") Long carteId) {
        ResponseEntity<?> carte = carteClient.fetchCarte(compteId.toString(),carteId.toString());
        return carte;
    }

    // POST new carte
    @PostMapping(value = "/{compteId}/cartes")
    public ResponseEntity<?> newCarte(@PathVariable("compteId") Long compteId, @RequestBody Carte carte) {
        HttpEntity<Carte> test = new HttpEntity<>(carte);
        ResponseEntity<?> res = carteClient.postCarte(compteId.toString(),test);
        return res;
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
