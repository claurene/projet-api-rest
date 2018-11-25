package fr.miage.m2.bankservice.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PaysDevise {
    @Id
    private Long id;

    private String pays;
    private String devise;

    public PaysDevise() {
    }

    public PaysDevise(String pays, String devise) {
        this.pays = pays;
        this.devise = devise;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }
}
