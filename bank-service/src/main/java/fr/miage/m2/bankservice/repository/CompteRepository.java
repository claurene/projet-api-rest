package fr.miage.m2.bankservice.repository;

import fr.miage.m2.bankservice.model.Compte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompteRepository extends JpaRepository<Compte,String> {
    Optional<Compte> findByIban(String iban);

}
