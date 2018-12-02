package fr.miage.m2.bankservice.proxy;

import com.netflix.discovery.EurekaClient;
import fr.miage.m2.bankservice.model.User;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthClient {

    private final RestTemplate restTemplate;

    private final EurekaClient eurekaClient;

    public AuthClient(RestTemplateBuilder builder, EurekaClient eurekaClient) {
        this.restTemplate = builder.build(); // no bean needed
        this.eurekaClient = eurekaClient;
    }

    // sign up
    public ResponseEntity<?> signUp (HttpEntity<User> entity){
        return this.restTemplate.postForEntity(getUrl()+"/users/sign-up",entity,String.class);
    }

    // login
    public ResponseEntity<?> login (HttpEntity<User> entity){
        return this.restTemplate.postForEntity(getUrl()+"/login",entity,String.class);
    }

    // Méthodes private

    // Permet le load balancing
    private String getUrl(){
        return eurekaClient.getNextServerFromEureka("auth-service",false).getHomePageUrl();
    }
}
