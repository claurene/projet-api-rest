package fr.miage.m2.cartesservice.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Carte {
    @Id
    @GeneratedValue
    private Long id;

    private String numcarte;
    private String code;
    //TODO: à completer

    private Long compteid;

    public Carte() {
    }

    public Carte(String numcarte, String code, Long compteId) {
        this.numcarte = numcarte;
        this.code = code;
        this.compteid = compteId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getCompteId() {
        return compteid;
    }

    public void setCompteId(Long compteId) {
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
