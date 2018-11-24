package fr.miage.m2.bankservice.proxy;

import fr.miage.m2.bankservice.controller.CompteController;
import fr.miage.m2.bankservice.model.Carte;
import fr.miage.m2.bankservice.proxy.config.CarteConfig;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class CarteClient {

    private final CarteConfig config;
    private final RestTemplate restTemplate;

    private final String CARTES_URL;

    public CarteClient(CarteConfig config, RestTemplateBuilder builder) {
        this.config = config;
        this.restTemplate = builder.build(); // no bean needed
        this.CARTES_URL = config.getUrl()+":"+config.getPort()+"/comptes/{compteId}/cartes";
    }

    // GET all cartes
    public ResponseEntity<?> fetchCartes(String compteId){
        //TODO: fix liens HATEOAS
        Carte[] res = this.restTemplate.getForObject(CARTES_URL,Carte[].class,compteId);
        return new ResponseEntity<>(cartesToResource(res,compteId),HttpStatus.OK);
    }

    // GET one carte
    public ResponseEntity<?> fetchCarte (String compteId, String carteId){
        //return this.restTemplate.getForEntity(CARTES_URL+"/{carteId}",Carte.class,compteId,carteId);
        Carte carte = this.restTemplate.getForObject(CARTES_URL+"/{carteId}",Carte.class,compteId,carteId);
        return new ResponseEntity<>(carteToResource(carte,compteId,carteId),HttpStatus.OK);
    }

    // TODO: check methods

    // POST one carte
    public ResponseEntity<?> postCarte (String compteId, HttpEntity<Carte> entity){
        return this.restTemplate.postForEntity(CARTES_URL,entity,Carte.class,compteId);
    }

    // PUT one carte
    public ResponseEntity<?> putCarte (String compteId, String carteId, HttpEntity<Carte> entity){
        return this.restTemplate.exchange(CARTES_URL+"/{carteId}", HttpMethod.PUT,entity,Carte.class,compteId,carteId);
    }

    // DELETE one carte
    public ResponseEntity<?> deleteCarte (String compteId, String carteId){
        //String entityUrl = String.format(CARTES_URL+"/{carteId}",compteId,carteId);
        HttpHeaders responseHeader = new HttpHeaders(); // Génère un nouveau header pour la réponse
        HttpEntity<?> entity = new HttpEntity<>(responseHeader);
        return this.restTemplate.exchange(CARTES_URL+"/{carteId}", HttpMethod.DELETE,entity,Carte.class,compteId,carteId);
    }

    // Méthodes ToResource
    // TODO add links with rel

    private Resource<?> carteToResource(Carte carte, String compteId, String carteId) {
        Link selfLink = linkTo(methodOn(CompteController.class).getCarte(compteId,carteId)).withSelfRel();
        return new Resource<>(carte, selfLink);
    }

    private Resources<?> cartesToResource(Carte[] cartes, String compteId) {
        Link selfLink = linkTo(methodOn(CompteController.class).getAllCartes(compteId)).withSelfRel();
        List<Resource<?>> res = new ArrayList();
        Arrays.asList(cartes).forEach(c -> res.add(carteToResource(c,compteId,c.getId())));
        return new Resources<>(res,selfLink);
    }



}
