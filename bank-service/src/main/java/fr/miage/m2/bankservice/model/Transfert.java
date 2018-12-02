package fr.miage.m2.bankservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

// Model uniquement
public class Transfert {
    private String dateheure;
    private String IBAN;
    private String montant;

    @JsonCreator
    public Transfert(@JsonProperty("dateheure") String dateheure,@JsonProperty("iban") String IBAN,@JsonProperty("montant") String montant) {
        this.dateheure = dateheure;
        this.IBAN = IBAN;
        this.montant = montant;
    }

    public String getDateheure() {
        return dateheure;
    }

    public void setDateheure(String dateheure) {
        this.dateheure = dateheure;
    }

    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    public String getMontant() {
        return montant;
    }

    public void setMontant(String montant) {
        this.montant = montant;
    }
}
