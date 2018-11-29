package fr.miage.m2.bankservice.exception;

public class CountryNotFoundException extends Exception {
    public CountryNotFoundException() {
        super("Le pays est inconnu : le montant ne pourra pas être converti.");
    }
}
