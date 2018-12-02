package fr.miage.m2.cartesservice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
public class Carte {
    @Id
    private String id;

    @NotNull
    @Column(unique = true, length = 16)
    private String numcarte;
    @NotNull
    @Column(length = 16)
    private String code;
    @NotNull
    @Column(length = 16)
    private String cryptogramme;
    @NotNull
    private boolean bloquee;
    @NotNull
    private boolean localisation;
    @NotNull
    private BigDecimal plafond;
    @NotNull
    private boolean sanscontact;
    @NotNull
    private boolean virtuelle;

    @NotNull
    private String compteid;

    public Carte() {
    }

    public Carte(String numcarte, String code, String cryptogramme, boolean bloquee, boolean localisation, BigDecimal plafond, boolean sanscontact, boolean virtuelle, String compteid) {
        this.numcarte = numcarte;
        this.code = code;
        this.cryptogramme = cryptogramme;
        this.bloquee = bloquee;
        this.localisation = localisation;
        this.plafond = plafond;
        this.sanscontact = sanscontact;
        this.virtuelle = virtuelle;
        this.compteid = compteid;
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

    public String getCryptogramme() {
        return cryptogramme;
    }

    public void setCryptogramme(String cryptogramme) {
        this.cryptogramme = cryptogramme;
    }

    public boolean isBloquee() {
        return bloquee;
    }

    public void setBloquee(boolean bloquee) {
        this.bloquee = bloquee;
    }

    public boolean isLocalisation() {
        return localisation;
    }

    public void setLocalisation(boolean localisation) {
        this.localisation = localisation;
    }

    public BigDecimal getPlafond() {
        return plafond;
    }

    public void setPlafond(BigDecimal plafond) {
        this.plafond = plafond;
    }

    public boolean isSanscontact() {
        return sanscontact;
    }

    public void setSanscontact(boolean sanscontact) {
        this.sanscontact = sanscontact;
    }

    public boolean isVirtuelle() {
        return virtuelle;
    }

    public void setVirtuelle(boolean virtuelle) {
        this.virtuelle = virtuelle;
    }

    public String getCompteid() {
        return compteid;
    }

    public void setCompteid(String compteid) {
        this.compteid = compteid;
    }
}
