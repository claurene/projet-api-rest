package fr.miage.m2.bankservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

public class Compte implements Serializable{
    private final String id;

    private final String nom;
    private final String prenom;
    private final String iban;
    private final String pays;
    // TODO : à complèter

    @JsonCreator
    public Compte(@JsonProperty("id") String id,
                  @JsonProperty("nom") String nom,
                  @JsonProperty("prenom") String prenom,
                  @JsonProperty("iban") String iban,
                  @JsonProperty("pays") String pays) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.iban = iban;
        this.pays = pays;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }


    public String getPrenom() {
        return prenom;
    }


    public String getIban() {
        return iban;
    }


    public String getPays() {
        return pays;
    }

    @Override
    public String toString() {
        return "Compte{" +
                "id='" + id + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", iban='" + iban + '\'' +
                ", pays='" + pays + '\'' +
                '}';
    }
}
