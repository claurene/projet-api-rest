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
    private final String datenaissance;
    private final String pays;
    private final String nopassport;
    private final String numtel;
    private final String secret;
    private final String iban;


    @JsonCreator
    public Compte(@JsonProperty("id") String id,
				 @JsonProperty("nom") String nom,
				 @JsonProperty("prenom") String prenom,
				 @JsonProperty("datenaissance") String datenaissance,
				 @JsonProperty("pays") String pays,
				 @JsonProperty("nopassport") String nopassport,
				 @JsonProperty("numtel") String numtel,
				 @JsonProperty("secret") String secret,
				 @JsonProperty("iban") String iban) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.datenaissance = datenaissance;
        this.pays = pays;
        this.nopassport = nopassport;
        this.numtel = numtel;
        this.secret = secret;
        this.iban = iban;
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

    public String getDatenaissance() {
        return datenaissance;
    }

    public String getNopassport() {
        return nopassport;
    }

    public String getNumtel() {
        return numtel;
    }

    public String getSecret() {
        return secret;
    }
}
