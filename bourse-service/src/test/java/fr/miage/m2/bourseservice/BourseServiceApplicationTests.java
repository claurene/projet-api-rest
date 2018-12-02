package fr.miage.m2.bourseservice;

import fr.miage.m2.bourseservice.boundary.ValeurChangeRepository;
import fr.miage.m2.bourseservice.entity.ValeurChange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BourseServiceApplicationTests {

	@Test
	public void contextLoads() {
		// Vérifie que l'application se lance bien
		// Si ce test ne passe pas, lancer Eureka et reessayer
	}

	@Autowired
	private ValeurChangeRepository vcr;
	@Autowired
	private TestRestTemplate restTemplate; // rôle du client de l'API

	private final String compteid = UUID.randomUUID().toString();

	@Before
	public void setupContent() {
		vcr.deleteAll(); // On réinitialise le repository
	}

	@Test
	public void getValeurDeChange() {
		ValeurChange vc1 = new ValeurChange(new Long(10001),"USD","EUR",new BigDecimal(1.1284));
		vcr.save(vc1);

		ResponseEntity<String> response = restTemplate.getForEntity("/change-devise/source/{source}/cible/{cible}", String.class,vc1.getSource(),vc1.getCible());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("1.13");
	}

	@Test
	public void getMontant(){
		ValeurChange vc1 = new ValeurChange(new Long(10001),"USD","EUR",new BigDecimal(1.1284));
		vcr.save(vc1);

		ResponseEntity<String> response = restTemplate.getForEntity("/change-devise/source/{source}/cible/{cible}/quantite/{qte}", String.class,vc1.getSource(),vc1.getCible(),1000);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("1130.0");
	}

}
