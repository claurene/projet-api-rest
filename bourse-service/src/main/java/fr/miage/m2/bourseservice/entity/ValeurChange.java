package fr.miage.m2.bourseservice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class ValeurChange {

    @Id
    private Long id;
    @Column(name="devise_source")
    private String source;
    @Column(name="devise_cible")
    private String cible;
    private BigDecimal tauxConversion; //TODO: type ?

    public ValeurChange() {
        // JPA
    }

    public ValeurChange(Long id, String source, String cible, BigDecimal taux) {
        super();
        this.id = id;
        this.source = source;
        this.cible = cible;
        this.tauxConversion = taux;
    }

    public Long getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public String getCible() {
        return cible;
    }

    public BigDecimal getTauxConversion() {
        return tauxConversion;
    }

}