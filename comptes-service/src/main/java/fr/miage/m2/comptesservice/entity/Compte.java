package fr.miage.m2.comptesservice.entity;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Compte implements Serializable{

    @Id
    @JsonProperty("id")
    private String id;

    private String nom;
    private String prenom;
    private String iban;
    private String pays;
    // TODO : à complèter

    public Compte() {
    }

    public Compte(String nom, String prenom, String iban, String pays) {
        this.nom = nom;
        this.prenom = prenom;
        this.iban = iban;
        this.pays = pays;
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
