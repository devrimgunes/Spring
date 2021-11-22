package com.javaegitimleri.petClinic.web;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.javaegitimleri.petclinic.model.Owner;

public class PetClinicRestControllerTests {
	@Autowired
	private RestTemplate restTemplate;

	@Before
	public void setUp() {
		restTemplate = new RestTemplate();
	}

	@Test
	public void testGetOwnerById() {
		ResponseEntity<Owner> response = restTemplate.getForEntity("http://localhost:8080/rest/owner/1", Owner.class);
		MatcherAssert.assertThat(response.getStatusCodeValue(), Matchers.equalTo(200));
		MatcherAssert.assertThat(response.getBody().getFirstName(), Matchers.equalTo("Ziya"));
	}

	@Test
	public void testGetOwnersByLastName() {
		ResponseEntity<List> response = restTemplate.getForEntity("http://localhost:8080/rest/owner?ln=Gunes",
				List.class);

		MatcherAssert.assertThat(response.getStatusCodeValue(), Matchers.equalTo(200));
		
		List<Map<String, String>> body = response.getBody();

		List<String> firstNames = body.stream().map(e -> e.get("firstName")).collect(Collectors.toList());

		MatcherAssert.assertThat(firstNames, Matchers.containsInAnyOrder("Ali", "Arda", "Lale"));
	}
	
	@Test
	public void testGetOwners() {
		ResponseEntity<List> response = restTemplate.getForEntity("http://localhost:8080/rest/owners", List.class);
		
		MatcherAssert.assertThat(response.getStatusCodeValue(), Matchers.equalTo(200));
		
		List<Map<String,String>> body = response.getBody();
		
		List<String> firstNames = body.stream().map(e->e.get("firstName")).collect(Collectors.toList());
		
		MatcherAssert.assertThat(firstNames, Matchers.containsInAnyOrder("Devrim", "Ela", "Sezer", "Zeynep"));
	}
	
	@Test
	public void testCreateOwner() {
		Owner owner = new Owner();
		owner.setFirstName("Aslı");
		owner.setLastName("Taha");

		URI location = restTemplate.postForLocation("http://localhost:8080/rest/owner", owner);

		Owner owner2 = restTemplate.getForObject(location, Owner.class);

		MatcherAssert.assertThat(owner2.getFirstName(), Matchers.equalTo(owner.getFirstName()));
		MatcherAssert.assertThat(owner2.getLastName(), Matchers.equalTo(owner.getLastName()));
	}
	
	@Test
	public void testUpdateOwner() {
		Owner owner = restTemplate.getForObject("http://localhost:8080/rest/owner/4", Owner.class);

		MatcherAssert.assertThat(owner.getFirstName(), Matchers.equalTo("Ela"));

		owner.setFirstName("Ela Bal");
		restTemplate.put("http://localhost:8080/rest/owner/4", owner);
		owner = restTemplate.getForObject("http://localhost:8080/rest/owner/4", Owner.class);

		MatcherAssert.assertThat(owner.getFirstName(), Matchers.equalTo("Ela Bal"));
	}
	
	@Test
	public void testDeleteOwner() {
		restTemplate.delete("http://localhost:8080/rest/owner/1");
		try {
			restTemplate.getForEntity("http://localhost:8080/rest/owner/1", Owner.class);
			Assert.fail("should have not returned owner");
			} catch(HttpClientErrorException ex) {
				MatcherAssert.assertThat(ex.getStatusCode().value(), Matchers.equalTo(404));
			}
		}
}
