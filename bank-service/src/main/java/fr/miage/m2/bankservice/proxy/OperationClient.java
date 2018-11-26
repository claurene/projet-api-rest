package fr.miage.m2.bankservice.proxy;

import fr.miage.m2.bankservice.controller.BankController;
import fr.miage.m2.bankservice.model.Operation;
import fr.miage.m2.bankservice.proxy.config.OperationConfig;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class OperationClient {

    private final OperationConfig config;
    private final RestTemplate restTemplate;

    private final String OPERATIONS_URL;

    public OperationClient(OperationConfig config, RestTemplateBuilder builder) {
        this.config = config;
        this.restTemplate = builder.build(); // no bean needed
        this.OPERATIONS_URL = config.getUrl()+":"+config.getPort()+"/comptes/{compteId}/operations";
    }

    // GET all operations
    // TODO: filter parameter
    public ResponseEntity<?> fetchOperations(String compteId,
                                             Optional<String> categorie,
                                             Optional<String> commercant,
                                             Optional<String> pays){
        //TODO: fix liens HATEOAS
        // Filtering
        ArrayList<String> params = new ArrayList<>();
        if (categorie.isPresent()) {
            params.add("categorie="+categorie.get());
        }
        if (commercant.isPresent()) {
            params.add("commercant="+commercant.get());
        }
        if (pays.isPresent()) {
            params.add("pays="+pays.get());
        }
        String url = OPERATIONS_URL+"?";
        url+= params.stream().collect(Collectors.joining("&"));
        Operation[] res = this.restTemplate.getForObject(url,Operation[].class,compteId);
        return new ResponseEntity<>(operationsToResource(res,compteId), HttpStatus.OK);
    }

    // GET one operation
    public ResponseEntity<?> fetchOperation (String compteId, String operationId){
        Operation operation = this.restTemplate.getForObject(OPERATIONS_URL +"/{operationId}",Operation.class,compteId,operationId);
        return new ResponseEntity<>(operationToResource(operation,compteId,operationId),HttpStatus.OK);
    }

    // POST one operation
    public ResponseEntity<?> postOperation (String compteId, HttpEntity<Operation> entity){
        URI uri = this.restTemplate.postForLocation(OPERATIONS_URL, entity, compteId);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(BankController.class).slash(uri.getPath()).toUri()); //TODO: remove '/comptes' from controller uri (or use another class)
        return new ResponseEntity<>(null,headers,HttpStatus.CREATED);
    }

    // GET solde
    public ResponseEntity<?> getSolde (String compteId) {
        // TODO: response entity + add HATEOAS links
        return this.restTemplate.getForEntity(OPERATIONS_URL + "/solde", String.class, compteId);
    }

    // Méthodes ToResource

    private Resource<?> operationToResource(Operation operation, String compteId, String operationId) {
        Link selfLink = linkTo(methodOn(BankController.class).getOperation(compteId,operationId)).withSelfRel();
        // TODO: voir si utile d'ajouter d'autres liens HATEOAS
        /*Resource res = new Resource<>(operation, selfLink);
        res.add(linkTo(methodOn(CompteController.class).getAllOperations(compteId,null,null,null)).withRel("operations"));
        res.add(linkTo(methodOn(CompteController.class).getCompte(compteId)).withRel("compte"));
        return res;*/
        return new Resource<>(operation, selfLink);
    }

    private Resources<?> operationsToResource(Operation[] operations, String compteId) {
        Link selfLink = linkTo(methodOn(BankController.class).getAllOperations(compteId,null,null,null)).withSelfRel();
        List<Resource<?>> res = new ArrayList();
        Arrays.asList(operations).forEach(c -> res.add(operationToResource(c,compteId,c.getId())));
        return new Resources<>(res,selfLink);
    }



}