package fr.miage.m2.authservice.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UserExtended extends User {
    private String compteId;

    public UserExtended(String username, String password, Collection<? extends GrantedAuthority> authorities, String compteId) {
        super(username, password, authorities);
        this.compteId = compteId;
    }

    public String getCompteId() {
        return compteId;
    }

    public void setCompteId(String compteId) {
        this.compteId = compteId;
    }
}
