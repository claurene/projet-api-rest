package fr.miage.m2.cartesservice.boundary;

import fr.miage.m2.cartesservice.entity.Carte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "cartes")
public interface CarteRepository extends JpaRepository<Carte,String> {
    Iterable<Carte> findAllByCompteid(String id);
    Optional<Carte> findByIdAndCompteid(String id, String compteid);

    Boolean existsByIdAndCompteid(String id, String compteid);
}
