package fr.miage.m2.bourseservice.boundary;

import fr.miage.m2.bourseservice.entity.ValeurChange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValeurChangeRepository extends JpaRepository<ValeurChange, Long> {

    ValeurChange findBySourceAndCible(String source, String cible);

}
