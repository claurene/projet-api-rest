package fr.miage.m2.bourseservice.boundary;

import fr.miage.m2.bourseservice.entity.ValeurChange;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class BourseController {

    private ValeurChangeRepository vcr;

    public BourseController(ValeurChangeRepository vcr) {
        this.vcr = vcr;
    }

    @GetMapping("/change-devise/source/{source}/cible/{cible}")
    public BigDecimal getValeurDeChange(@PathVariable String source, @PathVariable String cible) {

        ValeurChange valeurChange = vcr.findBySourceAndCible(source, cible);

        if (valeurChange == null) {
            return null;
        } else {
            return valeurChange.getTauxConversion();
        }
    }

    @GetMapping("/change-devise/source/{source}/cible/{cible}/quantite/{qte}")
    public BigDecimal getMontant(@PathVariable String source, @PathVariable String cible, @PathVariable BigDecimal qte) {

        ValeurChange valeurChange = vcr.findBySourceAndCible(source, cible);

        if (valeurChange == null) {
            return null;
        } else {
            return qte.multiply(valeurChange.getTauxConversion());
        }
    }

}
