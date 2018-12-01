package fr.miage.m2.bankservice.proxy;

import com.netflix.discovery.EurekaClient;
import fr.miage.m2.bankservice.controller.BankController;
import fr.miage.m2.bankservice.model.Operation;
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

    //private final OperationConfig config;
    private final RestTemplate restTemplate;

    private final EurekaClient eurekaClient;

    private final String OPERATIONS_URL = "/comptes/{compteId}/operations";

    public OperationClient(RestTemplateBuilder builder, EurekaClient eurekaClient) {
        //this.config = config;
        this.restTemplate = builder.build(); // no bean needed
        //this.getUrl()+OPERATIONS_URL = config.getUrl()+":"+config.getPort()+"/comptes/{compteId}/operations";
        this.eurekaClient = eurekaClient;
    }

    // GET all operations
    public ResponseEntity<?> fetchOperations(String compteId,
                                             Optional<String> categorie,
                                             Optional<String> commercant,
                                             Optional<String> pays){
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
        String url = getUrl()+OPERATIONS_URL+"?";
        url+= params.stream().collect(Collectors.joining("&"));
        Operation[] res = this.restTemplate.getForObject(url,Operation[].class,compteId);
        return new ResponseEntity<>(operationsToResource(res,compteId), HttpStatus.OK);
    }

    // GET one operation
    public ResponseEntity<?> fetchOperation (String compteId, String operationId){
        Operation operation = this.restTemplate.getForObject(getUrl()+OPERATIONS_URL +"/{operationId}",Operation.class,compteId,operationId);
        return new ResponseEntity<>(operationToResource(operation,compteId,operationId),HttpStatus.OK);
    }

    // POST one operation
    public ResponseEntity<?> postOperation (String compteId, HttpEntity<Operation> entity){
        URI uri = this.restTemplate.postForLocation(getUrl()+OPERATIONS_URL, entity, compteId);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(BankController.class).slash(uri.getPath()).toUri());
        return new ResponseEntity<>(null,headers,HttpStatus.CREATED);
    }

    // GET solde
    public ResponseEntity<?> getSolde (String compteId) {
        // TODO: response entity + add HATEOAS links ?
        return this.restTemplate.getForEntity(getUrl()+OPERATIONS_URL + "/solde", String.class, compteId);
    }

    // Méthodes private

    private Resource<?> operationToResource(Operation operation, String compteId, String operationId) {
        Link selfLink = linkTo(methodOn(BankController.class).getOperation(compteId,operationId)).withSelfRel();
        Resource res = new Resource<>(operation, selfLink);
        res.add(linkTo(methodOn(BankController.class).getAllOperations(compteId,null,null,null)).withRel("operations"));
        res.add(linkTo(methodOn(BankController.class).getCompte(compteId)).withRel("compte"));
        return res;
        //return new Resource<>(operation, selfLink);
    }

    private Resources<?> operationsToResource(Operation[] operations, String compteId) {
        Link selfLink = linkTo(methodOn(BankController.class).getAllOperations(compteId,null,null,null)).withSelfRel();
        List<Resource<?>> res = new ArrayList();
        Arrays.asList(operations).forEach(c -> res.add(operationToResource(c,compteId,c.getId())));
        return new Resources<>(res,selfLink);
    }

    private String getUrl(){
        return eurekaClient.getNextServerFromEureka("operations-service",false).getHomePageUrl();
    }


}