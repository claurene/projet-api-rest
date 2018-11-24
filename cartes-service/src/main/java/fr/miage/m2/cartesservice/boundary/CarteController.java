package fr.miage.m2.cartesservice.boundary;

import fr.miage.m2.cartesservice.entity.Carte;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping(value="/comptes/{compteId}/cartes", produces = MediaType.APPLICATION_JSON_VALUE)
public class CarteController {

    private CarteRepository cr;

    public CarteController(CarteRepository cr) {
        this.cr = cr;
    }

    // GET all (by compte ID)
    @GetMapping
    public ResponseEntity<?> getAllCartesByCompteId(@PathVariable("compteId") String compteId) {
        Iterable<Carte> allCartes = cr.findAllByCompteid(compteId);
        return new ResponseEntity<>(allCartes, HttpStatus.OK);
        // TODO: fix carteToResource(allCartes,compteId) for standalone mode
    }


    // GET one
    @GetMapping(value = "/{carteId}")
    public ResponseEntity<?> getCarte(@PathVariable("compteId") String compteId, @PathVariable("carteId") String id) {
        return Optional.ofNullable(cr.findByIdAndCompteid(id,compteId))
                .filter(Optional::isPresent)
                .map(i -> new ResponseEntity<>(carteToResource(i.get(), true,compteId), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // POST (create)
    @PostMapping
    public ResponseEntity<?> newCarte(@PathVariable("compteId") String compteId, @RequestBody Carte carte) {
        carte.setId(UUID.randomUUID().toString()); // Donne un nouvel identifiant
        carte.setCompteId(compteId);
        Carte saved = cr.save(carte); // Fait persister l'carte
        HttpHeaders responseHeader = new HttpHeaders(); // Génère un nouveau header pour la réponse
        //TODO fix - responseHeader.setLocation(linkTo(CarteController.class).slash(saved.getId()).toUri()); // La localisation (URI) de l'carte est un lien vers sa classe, ajoute un '/' et renvoi l'identifiant (cartes/123abc...)
        return new ResponseEntity<>(null, responseHeader, HttpStatus.CREATED);
    }

    // PUT (update)
    // TODO: changer méthode ? pcq là ça remplace tout donc bof secure + champs null si pas toutes les infos sont envoyées -> PATCH ?
    @PutMapping(value="/{carteId}")
    public ResponseEntity<?> putCarte(@RequestBody Carte carte, @PathVariable("compteId") String compteId, @PathVariable("carteId") String id) {
        // On change l'id afin de remplacer l'objet
        Optional<Carte> body = Optional.ofNullable(carte);
        if (!body.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // N'est pas créé si il n'existe pas
        }
        if (!cr.existsByIdAndCompteid(id,compteId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        carte.setId(id);
        carte.setCompteId(compteId);
        Carte saved = cr.save(carte);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // DELETE
    @DeleteMapping(value="/{carteId}")
    public ResponseEntity<?> deleteCarte(@PathVariable("compteId") String compteId, @PathVariable("carteId") String id) {
        Optional<Carte> carte = cr.findByIdAndCompteid(id,compteId);
        if (carte.isPresent()) {
            cr.delete(carte.get());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Méthodes ToResource

    private Resources<Resource<Carte>> carteToResource(Iterable<Carte> cartes, String compteId) {
        Link selfLink = linkTo(methodOn(CarteController.class).getAllCartesByCompteId(compteId)).withSelfRel();
        // Liens référencant chaque carte dans la collection
        List<Resource<Carte>> carteResources = new ArrayList<>();
        cartes.forEach(carte ->
                carteResources.add(carteToResource(carte, false,compteId)));
        return new Resources<>(carteResources, selfLink);
    }

    private Resource<Carte> carteToResource(Carte carte, Boolean collection, String compteId) {
        Link selfLink = linkTo(CarteRepository.class)
                .slash(carte.getId()) //TODO: fix
                .withSelfRel();
        if (collection) {
            Link collectionLink = linkTo(methodOn(CarteController.class).getAllCartesByCompteId(compteId)).withSelfRel();
            return new Resource<>(carte, selfLink, collectionLink);
        } else {
            return new Resource<>(carte, selfLink);
        }
    }
    
}
