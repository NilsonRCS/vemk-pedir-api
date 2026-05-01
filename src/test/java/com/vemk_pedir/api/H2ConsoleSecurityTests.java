package com.vemk_pedir.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class H2ConsoleSecurityTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@Test
	void h2ConsoleIsAccessibleWithoutAuthentication() throws Exception {
		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/h2-console/", String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("SAMEORIGIN", response.getHeaders().getFirst("X-Frame-Options"));
		assertTrue(response.getBody() != null && response.getBody().contains("H2 Console"));
	}
}