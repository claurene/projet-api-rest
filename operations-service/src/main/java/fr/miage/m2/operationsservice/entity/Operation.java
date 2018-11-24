package fr.miage.m2.operationsservice.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Operation {
    @Id
    private String id;

    private String libelle;
    private float montant;
    //TODO: à completer

    private String compteid;

    public Operation() {
    }

    public Operation(String libelle, float montant, String compteid) {
        this.libelle = libelle;
        this.montant = montant;
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
}
