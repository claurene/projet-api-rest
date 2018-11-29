package fr.miage.m2.bankservice.exception;

public class DeviseNotFoundException extends Exception {
    public DeviseNotFoundException() {
        super("La devise du pays est inconnue : le montant ne pourra pas être converti.");
    }
}
