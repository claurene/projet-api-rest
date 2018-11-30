package fr.miage.m2.bourseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class BourseServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BourseServiceApplication.class, args);
	}
}
