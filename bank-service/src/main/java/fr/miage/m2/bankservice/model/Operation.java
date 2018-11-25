package fr.miage.m2.bankservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Operation {
    private final String id;
    private String dateheure;
    private String libelle;
    private float montant;
    private String commercant;
    private String categorie;
    private String pays;
    //TODO: taux

    private final String compteid;

    @JsonCreator
    public Operation(@JsonProperty("id") String id,
                     @JsonProperty("dateheure") String dateheure,
                     @JsonProperty("libelle") String libelle,
                     @JsonProperty("montant") float montant,
                     @JsonProperty("commercant") String commercant,
                     @JsonProperty("categorie") String categorie,
                     @JsonProperty("pays") String pays,
                     @JsonProperty("compteId") String compteid) {
        this.id = id;
        this.dateheure = dateheure;
        this.libelle = libelle;
        this.montant = montant;
        this.commercant = commercant;
        this.categorie = categorie;
        this.pays = pays;
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
}
