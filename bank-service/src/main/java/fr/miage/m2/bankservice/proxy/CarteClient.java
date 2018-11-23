package fr.miage.m2.bankservice.proxy;

import fr.miage.m2.bankservice.controller.CompteController;
import fr.miage.m2.bankservice.model.Carte;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
    public ResponseEntity<String> fetchCartes(String compteId){
        return this.restTemplate.getForEntity(CARTES_URL,String.class,compteId);
    }

    // GET one carte
    public ResponseEntity<?> fetchCarte (String compteId, String carteId){
        //return this.restTemplate.getForEntity(CARTES_URL+"/{carteId}",Carte.class,compteId,carteId);
        Carte carte = this.restTemplate.getForObject(CARTES_URL+"/{carteId}",Carte.class,compteId,carteId);
        return new ResponseEntity<>(carteToResource(carte,Long.decode(compteId),Long.decode(carteId)),HttpStatus.OK);
    }

    // TODO: check methods

    // POST one carte
    public ResponseEntity<?> postCarte (String compteId, HttpEntity<Carte> entity){
        return this.restTemplate.postForEntity(CARTES_URL,entity,Carte.class,compteId);
    }

    /*
    // PUT one carte
    public ResponseEntity<String> putCarte (String compteId, String carteId, HttpEntity<String> entity){
        return this.restTemplate.exchange(CARTES_URL+"/{carteId}", HttpMethod.PUT,entity,String.class,compteId,carteId);
    }

    // DELETE one carte
    public ResponseEntity<String> putCarte (String compteId, String carteId){
        String entityUrl = String.format(CARTES_URL+"/{carteId}",compteId,carteId);
        this.restTemplate.delete(entityUrl);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }*/

    // Méthodes ToResource
    // TODO add links with rel

    private Resource<Carte> carteToResource(Carte carte, Long compteId, Long carteId) {
        Link selfLink = linkTo(methodOn(CompteController.class).getCarte(compteId,carteId)).withSelfRel();
        return new Resource<>(carte, selfLink);
    }



}
