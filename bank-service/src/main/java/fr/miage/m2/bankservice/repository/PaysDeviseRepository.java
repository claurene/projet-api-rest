package fr.miage.m2.bankservice.repository;

import fr.miage.m2.bankservice.model.PaysDevise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaysDeviseRepository extends JpaRepository<PaysDevise,Long> {
    Optional<PaysDevise> findByPays(String pays);
}
