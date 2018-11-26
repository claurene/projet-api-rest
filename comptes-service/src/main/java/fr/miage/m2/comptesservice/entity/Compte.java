package fr.miage.m2.comptesservice.entity;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class Compte {

    @Id
    private String id;

    @NotNull
    private String nom;
    @NotNull
    private String prenom;
    @NotNull
    private String datenaissance; //TODO: format ?
    @NotNull
    private String pays;
    @NotNull
    private String nopassport;
    @NotNull
    private String numtel; //TODO: format ?
    @NotNull
    private String secret;
    @NotNull
    private String iban;

    public Compte() {
    }

    public Compte(String nom, String prenom, String datenaissance, String pays, String nopassport, String numtel, String secret, String iban) {
        this.nom = nom;
        this.prenom = prenom;
        this.datenaissance = datenaissance;
        this.pays = pays;
        this.nopassport = nopassport;
        this.numtel = numtel;
        this.secret = secret;
        this.iban = iban;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getDatenaissance() {
        return datenaissance;
    }

    public void setDatenaissance(String datenaissance) {
        this.datenaissance = datenaissance;
    }

    public String getNopassport() {
        return nopassport;
    }

    public void setNopassport(String nopassport) {
        this.nopassport = nopassport;
    }

    public String getNumtel() {
        return numtel;
    }

    public void setNumtel(String numtel) {
        this.numtel = numtel;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
