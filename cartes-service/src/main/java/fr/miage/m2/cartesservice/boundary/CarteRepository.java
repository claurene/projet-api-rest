package fr.miage.m2.cartesservice.boundary;

import fr.miage.m2.cartesservice.entity.Carte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "cartes")
public interface CarteRepository extends JpaRepository<Carte,Long> {
    Iterable<Carte> findAllByCompteid(Long id);
    Optional<Carte> findByIdAndCompteid(Long id, Long compteid);
}
