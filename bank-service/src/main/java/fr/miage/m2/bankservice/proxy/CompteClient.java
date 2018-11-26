package fr.miage.m2.bankservice.proxy;

import fr.miage.m2.bankservice.controller.BankController;
import fr.miage.m2.bankservice.model.Compte;
import fr.miage.m2.bankservice.proxy.config.CompteConfig;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class CompteClient {

    private final CompteConfig config;
    private final RestTemplate restTemplate;

    private final String COMPTES_URL;

    public CompteClient(CompteConfig config, RestTemplateBuilder builder) {
        this.config = config;
        this.restTemplate = builder.build(); // no bean needed
        this.COMPTES_URL = config.getUrl()+":"+config.getPort()+"/comptes";
    }

    // GET one compte
    public ResponseEntity<?> fetchCompte (String compteId){
        Compte compte = getCompteAsObject(compteId);
        return new ResponseEntity<>(compteToResource(compte,compteId), HttpStatus.OK);
    }

    // GET compte as object
    public Compte getCompteAsObject(String compteId) {
        return this.restTemplate.getForObject(COMPTES_URL+"/{compteId}",Compte.class,compteId);
    }

    // GET compte id by IBAN
    // Problèmes de routing si uniquement l'objet (Spring ne peut pas exposer l'id)
    public String getCompteIdByIban(String iban) {
        return this.restTemplate.getForObject(COMPTES_URL+"/iban/{iban}",String.class,iban);
    }

    // POST one compte
    public ResponseEntity<?> postCompte (HttpEntity<Compte> entity){
        URI uri = this.restTemplate.postForLocation(COMPTES_URL,entity);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(BankController.class).slash(uri.getPath()).toUri());
        return new ResponseEntity<>(null,headers,HttpStatus.CREATED);
    }

    // Méthodes "ToRessource"
    private Resource<Compte> compteToResource(Compte compte, String compteId) {
        Link selfLink = linkTo(methodOn(BankController.class).getCompte(compteId)).withSelfRel();
        Resource res = new Resource<>(compte,selfLink);
        res.add(linkTo(methodOn(BankController.class).getAllCartes(compteId)).withRel("cartes"));
        res.add(linkTo(methodOn(BankController.class).getAllOperations(compteId,null,null,null)).withRel("operations"));
        res.add(linkTo(methodOn(BankController.class).getSolde(compteId)).withRel("solde"));
        res.add(linkTo(methodOn(BankController.class).newTransfert(compteId,null)).withRel("transfert")); // TODO: relevant ??
        return res;
    }
}
