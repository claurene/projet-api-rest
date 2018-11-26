package fr.miage.m2.bankservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Carte {
    private final String id;
    private final String numcarte;
    private final String code;
    private final String cryptogramme;
    private final boolean bloquee;
    private final boolean localisation;
    private final BigDecimal plafond;
    private final boolean sanscontact;
    private final boolean virtuelle;

    private final String compteid;

    @JsonCreator
    public Carte(@JsonProperty("id") String id,
				 @JsonProperty("numcarte") String numcarte,
				 @JsonProperty("code") String code,
				 @JsonProperty("cryptogramme") String cryptogramme,
				 @JsonProperty("bloquee") boolean bloquee,
				 @JsonProperty("localisation") boolean localisation,
				 @JsonProperty("plafond") BigDecimal plafond,
				 @JsonProperty("sanscontact") boolean sanscontact,
				 @JsonProperty("virtuelle") boolean virtuelle,
				 @JsonProperty("compteid") String compteid) {
        this.id = id;
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

    public String getNumcarte() {
        return numcarte;
    }

    public String getCode() {
        return code;
    }

    public String getCompteid() {
        return compteid;
    }

    public String getCryptogramme() {
        return cryptogramme;
    }

    public boolean isBloquee() {
        return bloquee;
    }

    public boolean isLocalisation() {
        return localisation;
    }

    public BigDecimal getPlafond() {
        return plafond;
    }

    public boolean isSanscontact() {
        return sanscontact;
    }

    public boolean isVirtuelle() {
        return virtuelle;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }
}
