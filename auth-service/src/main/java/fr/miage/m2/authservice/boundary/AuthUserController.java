package fr.miage.m2.authservice.boundary;

import fr.miage.m2.authservice.entity.AuthUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
public class AuthUserController {

    private final AuthUserRepository applicationUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthUserController(AuthUserRepository applicationUserRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.applicationUserRepository = applicationUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @PostMapping("/users/sign-up")
    public void signUp(@RequestBody AuthUser user) {
        user.setCompteId(UUID.randomUUID().toString()); // génère le compteId qui va être envoyé à l'API
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        applicationUserRepository.save(user);
    }

    // Connexion avec spring oauth2 security
    @GetMapping("/users/me")
    public Principal user(Principal principal) {
        return principal;
    }

    // Remarque : login déjà implémenté via spring security, faire POST avec les credentials


}
