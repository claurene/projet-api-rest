package fr.miage.m2.cartesservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Carte {
    @Id
    private String id;

    private String numcarte;
    private String code;
    //TODO: à completer

    private String compteid;

    public Carte() {
    }

    public Carte(String numcarte, String code, String compteId) {
        this.numcarte = numcarte;
        this.code = code;
        this.compteid = compteId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumcarte() {
        return numcarte;
    }

    public void setNumcarte(String numcarte) {
        this.numcarte = numcarte;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCompteId() {
        return compteid;
    }

    public void setCompteId(String compteId) {
        this.compteid = compteId;
    }

    @Override
    public String toString() {
        return "Carte{" +
                "id=" + id +
                ", numcarte='" + numcarte + '\'' +
                ", code='" + code + '\'' +
                ", compteid=" + compteid +
                '}';
    }
}
