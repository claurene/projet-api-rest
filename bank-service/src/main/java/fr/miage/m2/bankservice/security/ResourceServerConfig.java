package fr.miage.m2.bankservice.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
public class ResourceServerConfig {
    @Configuration
    @EnableResourceServer
    protected static class ServerConfig extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.csrf().disable().authorizeRequests()
                    .antMatchers(HttpMethod.POST,"/sign-up").permitAll()
                    .antMatchers(HttpMethod.POST,"/login").permitAll()
                    .antMatchers("/comptes").authenticated();
        }
    }

}