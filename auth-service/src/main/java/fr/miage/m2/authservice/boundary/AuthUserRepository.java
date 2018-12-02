package fr.miage.m2.authservice.boundary;

import fr.miage.m2.authservice.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser,String> {

    AuthUser findByUsername(String username);
}
