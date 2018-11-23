package fr.miage.m2.bankservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Carte {
    private final String numcarte;
    private final String code;
    //TODO: à completer

    private final Long compteid;

    @JsonCreator
    public Carte(@JsonProperty("numcarte") String numcarte,
                 @JsonProperty("code") String code,
                 @JsonProperty("compteId") Long compteid) {
        this.numcarte = numcarte;
        this.code = code;
        this.compteid = compteid;
    }

    public String getNumcarte() {
        return numcarte;
    }

    public String getCode() {
        return code;
    }

    public Long getCompteid() {
        return compteid;
    }
}
