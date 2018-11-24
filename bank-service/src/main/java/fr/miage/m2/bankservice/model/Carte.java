package fr.miage.m2.bankservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Carte {
    private final String id;
    private final String numcarte;
    private final String code;
    //TODO: à completer

    private final String compteid;

    @JsonCreator
    public Carte(@JsonProperty("id") String id,
                 @JsonProperty("numcarte") String numcarte,
                 @JsonProperty("code") String code,
                 @JsonProperty("compteId") String compteid) {
        this.id = id;
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

    public String getCompteid() {
        return compteid;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }
}
