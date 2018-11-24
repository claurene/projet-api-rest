package fr.miage.m2.bankservice.proxy;

import fr.miage.m2.bankservice.controller.CompteController;
import fr.miage.m2.bankservice.model.Operation;
import fr.miage.m2.bankservice.proxy.config.OperationConfig;
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
    public ResponseEntity<?> fetchOperations(String compteId){
        //TODO: fix liens HATEOAS
        Operation[] res = this.restTemplate.getForObject(OPERATIONS_URL,Operation[].class,compteId);
        return new ResponseEntity<>(operationsToResource(res,compteId), HttpStatus.OK);
    }

    // GET one operation
    public ResponseEntity<?> fetchOperation (String compteId, String operationId){
        //return this.restTemplate.getForEntity(OPERATIONS_URL+"/{operationId}",Operation.class,compteId,operationId);
        Operation operation = this.restTemplate.getForObject(OPERATIONS_URL +"/{operationId}",Operation.class,compteId,operationId);
        return new ResponseEntity<>(operationToResource(operation,compteId,operationId),HttpStatus.OK);
    }

    // POST one operation
    public ResponseEntity<?> postOperation (String compteId, HttpEntity<Operation> entity){
        return this.restTemplate.postForEntity(OPERATIONS_URL,entity,Operation.class,compteId);
    }

    // GET solde
    public ResponseEntity<?> getSolde (String compteId) {
        // TODO: add HATEOAS links
        return this.restTemplate.getForEntity(OPERATIONS_URL + "/solde", String.class, compteId);
    }

    // Méthodes ToResource
    // TODO add links with rel

    private Resource<?> operationToResource(Operation operation, String compteId, String operationId) {
        Link selfLink = linkTo(methodOn(CompteController.class).getOperation(compteId,operationId)).withSelfRel();
        return new Resource<>(operation, selfLink);
    }

    private Resources<?> operationsToResource(Operation[] operations, String compteId) {
        Link selfLink = linkTo(methodOn(CompteController.class).getAllOperations(compteId)).withSelfRel();
        List<Resource<?>> res = new ArrayList();
        Arrays.asList(operations).forEach(c -> res.add(operationToResource(c,compteId,c.getId())));
        return new Resources<>(res,selfLink);
    }



}