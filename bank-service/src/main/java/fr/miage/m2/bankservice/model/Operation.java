package fr.miage.m2.bankservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Operation {
    private final String id;
    private String dateheure;
    private String libelle;
    private float montant;
    private String commercant;
    private String categorie;
    private String pays;
    private BigDecimal taux;

    private final String compteid;

    @JsonCreator
    public Operation(@JsonProperty("id") String id,
                     @JsonProperty("dateheure") String dateheure,
                     @JsonProperty("libelle") String libelle,
                     @JsonProperty("montant") float montant,
                     @JsonProperty("commercant") String commercant,
                     @JsonProperty("categorie") String categorie,
                     @JsonProperty("pays") String pays,
                     @JsonProperty("taux") BigDecimal taux,
                     @JsonProperty("compteId") String compteid) {
        this.id = id;
        this.dateheure = dateheure;
        this.libelle = libelle;
        this.montant = montant;
        this.commercant = commercant;
        this.categorie = categorie;
        this.pays = pays;
        this.taux = taux;
        this.compteid = compteid;
    }

    public String getLibelle() {
        return libelle;
    }

    public float getMontant() {
        return montant;
    }

    public String getCompteid() {
        return compteid;
    }

    public String getDateheure() {
        return dateheure;
    }

    public String getCommercant() {
        return commercant;
    }

    public String getCategorie() {
        return categorie;
    }

    public String getPays() {
        return pays;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    public BigDecimal getTaux() {
        return taux;
    }

    public void setDateheure(String dateheure) {
        this.dateheure = dateheure;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public void setMontant(float montant) {
        this.montant = montant;
    }

    public void setCommercant(String commercant) {
        this.commercant = commercant;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public void setTaux(BigDecimal taux) {
        this.taux = taux;
    }
}
