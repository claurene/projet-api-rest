package fr.miage.m2.authservice.boundary;

import fr.miage.m2.authservice.entity.AuthUser;
import fr.miage.m2.authservice.entity.UserExtended;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.util.Collections.emptyList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private AuthUserRepository authUserRepository;
    public UserDetailsServiceImpl(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUser authUser = authUserRepository.findByUsername(username);
        if (authUser == null) {
            throw new UsernameNotFoundException(username);
        }
        return new UserExtended(authUser.getUsername(), authUser.getPassword(), emptyList(), authUser.getCompteId());
    }
}