package fr.miage.m2.cartesservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class CartesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CartesServiceApplication.class, args);
	}
}
