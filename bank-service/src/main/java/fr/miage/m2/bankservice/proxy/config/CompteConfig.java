package fr.miage.m2.bankservice.proxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("bank.comptes")
public class CompteConfig extends Config{
}
