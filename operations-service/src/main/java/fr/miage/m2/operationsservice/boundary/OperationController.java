package fr.miage.m2.operationsservice.boundary;

import fr.miage.m2.operationsservice.entity.Operation;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping(value="/comptes/{compteId}/operations", produces = MediaType.APPLICATION_JSON_VALUE)
public class OperationController {

    private OperationRepository or;

    public OperationController(OperationRepository or) {
        this.or = or;
    }

    // GET all (by compte ID)
    @GetMapping //(params = {"categorie","commercant","pays"})
    public ResponseEntity<?> getAllOperationsByCompteId(@PathVariable("compteId") String compteId,
                                                        @RequestParam("categorie") Optional<String> categorie,
                                                        @RequestParam("commercant") Optional<String> commercant,
                                                        @RequestParam("pays") Optional<String> pays ) {
        // Filtering
        Iterable<Operation> allOperations = or.findAllByFiltering(compteId,
                (categorie.isPresent() ? categorie.get() : null),
                (commercant.isPresent() ? commercant.get() : null),
                (pays.isPresent() ? pays.get() : null));
        return new ResponseEntity<>(allOperations, HttpStatus.OK);
    }

    // GET one
    @GetMapping(value = "/{operationId}")
    public ResponseEntity<?> getOperation(@PathVariable("compteId") String compteId, @PathVariable("operationId") String id) {
        return Optional.ofNullable(or.findByIdAndCompteid(id,compteId))
                .filter(Optional::isPresent)
                .map(i -> new ResponseEntity<>(i.get(), HttpStatus.OK)) 
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // POST (create)
    @PostMapping
    public ResponseEntity<?> newOperation(@PathVariable("compteId") String compteId, @RequestBody Operation operation) {
        operation.setId(UUID.randomUUID().toString()); // Donne un nouvel identifiant
        operation.setCompteid(compteId);
        Operation saved = or.save(operation); // Fait persister l'operation
        HttpHeaders responseHeader = new HttpHeaders(); // Génère un nouveau header pour la réponse
        responseHeader.setLocation(linkTo(methodOn(OperationController.class).getOperation(compteId,saved.getId())).toUri()); // La localisation (URI) de l'operation est un lien vers sa classe, ajoute un '/' et renvoi l'identifiant (operations/123abc...)
        return new ResponseEntity<>(null, responseHeader, HttpStatus.CREATED);
    }

    // GET solde
    // TODO: round 2 decimales ?
    @GetMapping(value = "/solde")
    public String getSolde(@PathVariable("compteId") String compteId) {
        return ""+or.findSoldeByCompteid(compteId);
    }

    // Méthodes ToResource

    /*private Resources<Resource<Operation>> operationToResource(Iterable<Operation> operations, String compteId) {
        Link selfLink = linkTo(methodOn(OperationController.class).getAllOperationsByCompteId(compteId,null,null,null)).withSelfRel();
        // Liens référencant chaque operation dans la collection
        List<Resource<Operation>> operationResources = new ArrayList<>();
        operations.forEach(operation ->
                operationResources.add(operationToResource(operation, false,compteId)));
        return new Resources<>(operationResources, selfLink);
    }

    private Resource<Operation> operationToResource(Operation operation, Boolean collection, String compteId) {
        Link selfLink = linkTo(OperationRepository.class)
                .slash(operation.getId()) //TODO: fix
                .withSelfRel();
        if (collection) {
            Link collectionLink = linkTo(methodOn(OperationController.class).getAllOperationsByCompteId(compteId,null,null,null)).withSelfRel();
            return new Resource<>(operation, selfLink, collectionLink);
        } else {
            return new Resource<>(operation, selfLink);
        }
    }*/
    
}
