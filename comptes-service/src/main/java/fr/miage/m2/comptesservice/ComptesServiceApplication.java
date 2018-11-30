package fr.miage.m2.comptesservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ComptesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComptesServiceApplication.class, args);
	}
}
