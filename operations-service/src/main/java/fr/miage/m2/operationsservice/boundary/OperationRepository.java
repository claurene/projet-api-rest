package fr.miage.m2.operationsservice.boundary;

import fr.miage.m2.operationsservice.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "operations")
public interface OperationRepository extends JpaRepository<Operation,String> {
    Iterable<Operation> findAllByCompteid(String id);
    Optional<Operation> findByIdAndCompteid(String id, String compteid);

    @Query("SELECT SUM(o.montant) FROM Operation o WHERE o.compteid = :compteid")
    String findSoldeByCompteid(@Param("compteid") String compteid);

    @Query("SELECT o FROM Operation o WHERE o.compteid = :compteid " +
            "AND (:cat is null or o.categorie = :cat)" +
            "AND (:com is null or o.commercant = :com)" +
            "AND (:pays is null or o.pays = :pays)")
    Iterable<Operation> findAllByFiltering(@Param("compteid") String compteid,
                                           @Param("cat") String cat,
                                           @Param("com") String com,
                                           @Param("pays") String pays);
}
