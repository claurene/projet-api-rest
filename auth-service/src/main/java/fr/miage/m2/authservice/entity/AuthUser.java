package fr.miage.m2.authservice.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "AUTHUSER")
public class AuthUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(unique = true)
    private String username;
    @NotNull
    private String password;

    private String compteid; // sera envoyé à l'API

    public AuthUser(String username, String password, String compteId) {
        this.username = username;
        this.password = password;
        this.compteid = compteId;
    }

    public AuthUser() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCompteId() {
        return compteid;
    }

    public void setCompteId(String compteId) {
        this.compteid = compteId;
    }
}
