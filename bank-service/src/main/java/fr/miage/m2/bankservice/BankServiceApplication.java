package fr.miage.m2.bankservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableEurekaClient
@EnableSwagger2
public class BankServiceApplication {

	// Regarde dans le package toutes les méthodes HTTP (GET/POST/PUT/DELETE etc.)
	@Bean
	public Docket swaggerBankApi() { // http://localhost:8080/swagger-ui.html
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("fr.miage.m2.bankservice"))
				.paths(PathSelectors.any())
				.build()
				.apiInfo(new ApiInfoBuilder()
						.version("1.0")
						.title("Bank API REST")
						.description("Documentation Bank API REST v1.0")
						.build()
				);
	}

	public static void main(String[] args) {
		SpringApplication.run(BankServiceApplication.class, args);
	}
}
