package fr.miage.m2.bankservice.proxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("bank.cartes") // va chercher les informations dans application.properties
public class CarteConfig extends Config{
}
