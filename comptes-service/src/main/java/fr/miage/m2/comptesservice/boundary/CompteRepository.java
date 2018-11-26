package fr.miage.m2.comptesservice.boundary;


import fr.miage.m2.comptesservice.entity.Compte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompteRepository extends JpaRepository<Compte,String> {
    Optional<Compte> findByIban(String iban);

}