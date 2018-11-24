package fr.miage.m2.bankservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Operation {
    private final String id;
    private final String libelle;
    private final float montant;
    //TODO: à completer

    private final String compteid;

    @JsonCreator
    public Operation(@JsonProperty("id") String id,
                 @JsonProperty("libelle") String libelle,
                 @JsonProperty("montant") float montant,
                 @JsonProperty("compteId") String compteid) {
        this.id = id;
        this.libelle = libelle;
        this.montant = montant;
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

    @JsonIgnore
    public String getId() {
        return id;
    }
}
