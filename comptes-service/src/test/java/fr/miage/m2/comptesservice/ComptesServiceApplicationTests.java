package fr.miage.m2.comptesservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.miage.m2.comptesservice.boundary.CompteRepository;
import fr.miage.m2.comptesservice.entity.Compte;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ComptesServiceApplicationTests {

	@Test
	public void contextLoads() {
		// Vérifie que l"application se lance bien
		// Si ce test ne passe pas, lancer Eureka et reessayer
	}

	@Autowired
	private CompteRepository cr;
	@Autowired
	private TestRestTemplate restTemplate; // rôle du client de l"API

	@Before
	public void setupContent() {
		cr.deleteAll(); // On réinitialise le repository
	}

	// GET compte
	@Test
	public void getCompte(){
		Compte c1 = new Compte(UUID.randomUUID().toString(),"Cat","Luna","29/03/2017","FR","264242448","0033712345678","secretlulu","738035816301840864711953934");
		cr.save(c1);

		ResponseEntity<String> response = restTemplate.getForEntity("/comptes/" + c1.getId(), String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("738035816301840864711953934");
	}

	// 404 compte not found
	@Test
	public void notFoundApi() {
		ResponseEntity<String> response = restTemplate.getForEntity("/comptes/150", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	// POST Compte
	@Test
	public void postCompte() throws Exception {
		Compte c1 = new Compte(UUID.randomUUID().toString(),"Cat","Luna","29/03/2017","FR","264242448","0033712345678","secretlulu","738035816301840864711953934");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<>(this.toJsonString(c1), headers);

		URI uri = this.restTemplate.postForLocation("/comptes",entity);

		ResponseEntity<String> response = restTemplate.getForEntity(uri.getPath(), String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("738035816301840864711953934");

	}

	// GET ID by iban
	@Test
	public void getCompteIdByIban(){
		Compte c1 = new Compte(UUID.randomUUID().toString(),"Cat","Luna","29/03/2017","FR","264242448","0033712345678","secretlulu","738035816301840864711953934");
		cr.save(c1);

		ResponseEntity<String> response = restTemplate.getForEntity("/comptes/iban/" + c1.getIban(), String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains(c1.getId());
	}

	///// Private /////

	// Transformer un objet en chaîne de caractères
	private String toJsonString(Object o) throws Exception {
		ObjectMapper map = new ObjectMapper();
		return map.writeValueAsString(o);
	}

}
