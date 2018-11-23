package fr.miage.m2.bankservice.repository;

import fr.miage.m2.bankservice.model.Compte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompteRepository extends JpaRepository<Compte,Long> {

}
