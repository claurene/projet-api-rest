package fr.miage.m2.operationsservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.miage.m2.operationsservice.boundary.OperationRepository;
import fr.miage.m2.operationsservice.entity.Operation;
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
public class OperationsServiceApplicationTests {

	@Test
	public void contextLoads() {
		// Vérifie que l"application se lance bien
		// Si ce test ne passe pas, lancer Eureka et reessayer
	}

	@Autowired
	private OperationRepository or;
	@Autowired
	private TestRestTemplate restTemplate; // rôle du client de l"API

	private final String compteid = UUID.randomUUID().toString();

	@Before
	public void setupContent() {
		or.deleteAll(); // On réinitialise le repository
	}

	// GET operations
	@Test
	public void getOperations(){
		Operation c1 = new Operation("01/08/2018","Salaire 07/2018",new BigDecimal("1833.54"),new BigDecimal("1.00"),"MIAGIC","Salaire","FR",compteid);
		c1.setId(UUID.randomUUID().toString());
		or.save(c1);
		Operation c2 = new Operation("01/09/2018","Salaire 08/2018",new BigDecimal("1833.54"),new BigDecimal("1.00"),"MIAGIC","Salaire","FR",compteid);
		c2.setId(UUID.randomUUID().toString());
		or.save(c2);

		ResponseEntity<String> response = restTemplate.getForEntity("/comptes/{compteId}/operations", String.class,compteid);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("Salaire 07/2018");
		assertThat(response.getBody()).contains("Salaire 08/2018");
	}

	// GET operation
	@Test
	public void getOperation(){
		Operation c1 = new Operation("01/08/2018","Salaire 07/2018",new BigDecimal("1833.54"),new BigDecimal("1.00"),"MIAGIC","Salaire","FR",compteid);
		c1.setId(UUID.randomUUID().toString());
		or.save(c1);

		ResponseEntity<String> response = restTemplate.getForEntity("/comptes/{compteId}/operations/" + c1.getId(), String.class,compteid);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("Salaire 07/2018");
	}

	// 404 operation not found
	@Test
	public void notFoundApi() {
		ResponseEntity<String> response = restTemplate.getForEntity("/comptes/{compteId}/operations/150", String.class,compteid);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	// POST Operation
	@Test
	public void postOperation() throws Exception {
		Operation c1 = new Operation("01/08/2018","Salaire 07/2018",new BigDecimal("1833.54"),new BigDecimal("1.00"),"MIAGIC","Salaire","FR",compteid);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<>(this.toJsonString(c1), headers);

		URI uri = this.restTemplate.postForLocation("/comptes/{compteId}/operations",entity,compteid);

		ResponseEntity<String> response = restTemplate.getForEntity(uri.getPath(), String.class,compteid);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("Salaire 07/2018");

	}
	
	// GET solde
	@Test
	public void getSolde() throws Exception {
		Operation c1 = new Operation("01/08/2018","Salaire 07/2018",new BigDecimal("1833.54"),new BigDecimal("1.00"),"MIAGIC","Salaire","FR",compteid);
		c1.setId(UUID.randomUUID().toString());
		or.save(c1);
		Operation c2 = new Operation("01/09/2018","Carrefour Contact",new BigDecimal("-833.54"),new BigDecimal("1.00"),"CARREFOUR","Alimentation","FR",compteid);
		c2.setId(UUID.randomUUID().toString());
		or.save(c2);

		ResponseEntity<String> response = restTemplate.getForEntity("/comptes/{compteId}/operations/solde", String.class,compteid);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("1000");
	}

	///// Private /////

	// Transformer un objet en chaîne de caractères
	private String toJsonString(Object o) throws Exception {
		ObjectMapper map = new ObjectMapper();
		return map.writeValueAsString(o);
	}

}
