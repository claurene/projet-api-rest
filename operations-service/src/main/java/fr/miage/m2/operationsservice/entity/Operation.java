package fr.miage.m2.operationsservice.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Operation {
    @Id
    private String id;

    private String libelle;
    private float montant;
    private String categorie;
    private String commercant;
    private String pays;
    //TODO: à completer

    private String compteid;

    public Operation() {
    }

    public Operation(String libelle, float montant, String categorie, String commercant, String pays, String compteid) {
        this.libelle = libelle;
        this.montant = montant;
        this.categorie = categorie;
        this.commercant = commercant;
        this.pays = pays;
        this.compteid = compteid;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public float getMontant() {
        return montant;
    }

    public void setMontant(float montant) {
        this.montant = montant;
    }

    public String getCompteid() {
        return compteid;
    }

    public void setCompteid(String compteid) {
        this.compteid = compteid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getCommercant() {
        return commercant;
    }

    public void setCommercant(String commercant) {
        this.commercant = commercant;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }
}
