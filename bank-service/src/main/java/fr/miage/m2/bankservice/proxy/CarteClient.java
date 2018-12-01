package fr.miage.m2.bankservice.proxy;

import com.netflix.discovery.EurekaClient;
import fr.miage.m2.bankservice.controller.BankController;
import fr.miage.m2.bankservice.model.Carte;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class CarteClient {

    //private final CarteConfig config;
    private final RestTemplate restTemplate;

    private final EurekaClient eurekaClient; //autowirred ?

    private final String CARTES_URL="/comptes/{compteId}/cartes";

    public CarteClient(RestTemplateBuilder builder, EurekaClient eurekaClient) {
        //this.config = config;
        this.restTemplate = builder.build(); // no bean needed
        //this.getUrl()+CARTES_URL = config.getUrl()+":"+config.getPort()+"/comptes/{compteId}/cartes";
        this.eurekaClient = eurekaClient;
    }

    // GET all cartes
    public ResponseEntity<?> fetchCartes(String compteId){
        Carte[] res = this.restTemplate.getForObject(getUrl()+CARTES_URL,Carte[].class,compteId);
        return new ResponseEntity<>(cartesToResource(res,compteId),HttpStatus.OK);
    }

    // GET one carte
    public ResponseEntity<?> fetchCarte (String compteId, String carteId){
        //return this.restTemplate.getForEntity(getUrl()+CARTES_URL+"/{carteId}",Carte.class,compteId,carteId);
        Carte carte = this.restTemplate.getForObject(getUrl()+CARTES_URL+"/{carteId}",Carte.class,compteId,carteId);
        return new ResponseEntity<>(carteToResource(carte,compteId,carteId),HttpStatus.OK);
    }

    // POST one carte
    public ResponseEntity<?> postCarte (String compteId, HttpEntity<Carte> entity){
        URI uri = this.restTemplate.postForLocation(getUrl()+CARTES_URL,entity,Carte.class,compteId);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(BankController.class).slash(uri.getPath()).toUri());
        return new ResponseEntity<>(null,headers,HttpStatus.CREATED);
    }

    // PUT one carte
    public ResponseEntity<?> putCarte (String compteId, String carteId, HttpEntity<Carte> entity){
        return this.restTemplate.exchange(getUrl()+CARTES_URL+"/{carteId}", HttpMethod.PUT,entity,Carte.class,compteId,carteId);
    }

    // DELETE one carte
    public ResponseEntity<?> deleteCarte (String compteId, String carteId){
        //String entityUrl = String.format(getUrl()+CARTES_URL+"/{carteId}",compteId,carteId);
        HttpHeaders responseHeader = new HttpHeaders(); // Génère un nouveau header pour la réponse
        HttpEntity<?> entity = new HttpEntity<>(responseHeader);
        return this.restTemplate.exchange(getUrl()+CARTES_URL+"/{carteId}", HttpMethod.DELETE,entity,Carte.class,compteId,carteId);
    }

    // Méthodes private

    private Resource<?> carteToResource(Carte carte, String compteId, String carteId) {
        Link selfLink = linkTo(methodOn(BankController.class).getCarte(compteId,carteId)).withSelfRel();
        Resource res = new Resource<>(carte,selfLink);
        res.add(linkTo(methodOn(BankController.class).getAllCartes(compteId)).withRel("cartes"));
        res.add(linkTo(methodOn(BankController.class).getCompte(compteId)).withRel("compte"));
        return res;
    }

    private Resources<?> cartesToResource(Carte[] cartes, String compteId) {
        Link selfLink = linkTo(methodOn(BankController.class).getAllCartes(compteId)).withSelfRel();
        List<Resource<?>> res = new ArrayList();
        Arrays.asList(cartes).forEach(c -> res.add(carteToResource(c,compteId,c.getId())));
        return new Resources<>(res,selfLink);
    }

    // Permet le load balancing
    private String getUrl(){
        String res = eurekaClient.getNextServerFromEureka("cartes-service",false).getHomePageUrl(); // false for security
        System.out.println(res); // debug : load balancing (java -Dserver.port=8086 -jar)
        return res;
    }

}
