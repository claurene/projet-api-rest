package fr.miage.m2.cartesservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import fr.miage.m2.cartesservice.boundary.CarteRepository;
import fr.miage.m2.cartesservice.entity.Carte;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

// Note : Eureka doit tourner pour que les tests passent !
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CartesServiceApplicationTests {

	@Test
	public void contextLoads() {
	    // Vérifie que l'application se lance bien
	    // Si ce test ne passe pas, lancer Eureka et reessayer
	}

	@Autowired
	private CarteRepository cr;
	@Autowired
	private TestRestTemplate restTemplate; // rôle du client de l'API

	private final String compteid = UUID.randomUUID().toString();

	@Before
	public void setupContent() {
		cr.deleteAll(); // On réinitialise le repository
	}

    // GET cartes
    @Test
    public void getCartes(){
        Carte c1 = new Carte("0822376073983937", "8902", "870",false,false,new BigDecimal(-1000),false,false,compteid);
        c1.setId(UUID.randomUUID().toString());
        cr.save(c1);
        Carte c2 = new Carte("7398393708223760", "0289", "123",false,false,new BigDecimal(-1000),false,false,compteid);
        c2.setId(UUID.randomUUID().toString());
        cr.save(c2);

        ResponseEntity<String> response = restTemplate.getForEntity("/comptes/{compteId}/cartes", String.class,compteid);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("0822376073983937");
        assertThat(response.getBody()).contains("7398393708223760");
    }

	// GET carte
	@Test
	public void getCarte(){
		Carte c1 = new Carte("0822376073983937", "8902", "870",false,false,new BigDecimal(-1000),false,false,compteid);
		c1.setId(UUID.randomUUID().toString());
		cr.save(c1);

		ResponseEntity<String> response = restTemplate.getForEntity("/comptes/{compteId}/cartes/" + c1.getId(), String.class,compteid);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("0822376073983937");
	}

	// 404 carte not found
    @Test
    public void notFoundApi() {
        ResponseEntity<String> response = restTemplate.getForEntity("/comptes/{compteId}/cartes/150", String.class,compteid);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // POST Carte
    @Test
    public void postCarte() throws Exception {
        Carte c1 = new Carte("0822376073983937", "8902", "870",false,false,new BigDecimal(-1000),false,false,compteid);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(this.toJsonString(c1), headers);

        URI uri = this.restTemplate.postForLocation("/comptes/{compteId}/cartes",entity,compteid);

        ResponseEntity<String> response = restTemplate.getForEntity(uri.getPath(), String.class,compteid);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("0822376073983937");

    }

    // PUT carte
    @Test
    public void putCarte() throws Exception {
        Carte c1 = new Carte("0822376073983937", "8902", "870",false,false,new BigDecimal(-1000),false,false,compteid);
        c1.setId(UUID.randomUUID().toString());
        cr.save(c1);
        c1.setBloquee(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(this.toJsonString(c1), headers);

        // Utilisation de l'API pour faire la modif
        restTemplate.put("/comptes/{compteId}/cartes/"+c1.getId(), entity,compteid);

        ResponseEntity<String> response = restTemplate.getForEntity("/comptes/{compteId}/cartes/" + c1.getId(), String.class,compteid);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("0822376073983937"); // On vérifie que ça n'a pas changé
        // On vérifie que le plafond a bien été modifiée
        Boolean bloquee = JsonPath.read(response.getBody(), "$.bloquee");
        assertThat(bloquee).isEqualTo(c1.isBloquee());
    }

    // DELETE carte
    @Test
    public void deleteCarte() throws Exception {
        Carte c1 = new Carte("0822376073983937", "8902", "870",false,false,new BigDecimal(-1000),false,false,compteid);
        c1.setId(UUID.randomUUID().toString());
        cr.save(c1);

        restTemplate.delete("/comptes/{compteId}/cartes/"+c1.getId(),compteid);

        ResponseEntity<String> response = restTemplate.getForEntity("/comptes/{compteId}/cartes/"+c1.getId(), String.class,compteid);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    ///// Private /////

    // Transformer un objet en chaîne de caractères
    private String toJsonString(Object o) throws Exception {
        ObjectMapper map = new ObjectMapper();
        return map.writeValueAsString(o);
    }

}
