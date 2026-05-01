package com.vemk_pedir.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PedidoFluxoTextoLivreTests {

    private static final Pattern ID_PATTERN = Pattern.compile("\\\"id\\\":(\\d+)");

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    void deveCriarPedidoEstruturadoAPartirDeTextoLivre() {
        String urlBase = "http://localhost:" + port;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = Map.of(
            "textoOriginal", "Quero 10 caixas de leite integral e 5 fardos de agua para entrega amanha",
            "cliente", "desconhecido"
        );

        ResponseEntity<String> postResponse = restTemplate
            .withBasicAuth("test", "test")
            .postForEntity(urlBase + "/pedido", new HttpEntity<>(payload, headers), String.class);

        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        assertNotNull(postResponse.getBody());
        assertTrue(postResponse.getBody() != null && postResponse.getBody().contains("\"produto\":\"leite integral\""));
        assertTrue(postResponse.getBody() != null && postResponse.getBody().contains("\"quantidade\":10"));
        assertTrue(postResponse.getBody() != null && postResponse.getBody().contains("\"produto\":\"agua\""));
        assertTrue(postResponse.getBody() != null && postResponse.getBody().contains("\"quantidade\":5"));
        assertTrue(postResponse.getBody() != null && postResponse.getBody().contains("\"dataEntrega\":\"" + LocalDate.now().plusDays(1) + "\""));

        Matcher matcher = ID_PATTERN.matcher(postResponse.getBody());
        assertTrue(matcher.find());
        String id = matcher.group(1);

        ResponseEntity<String> getResponse = restTemplate
            .withBasicAuth("test", "test")
            .getForEntity(urlBase + "/pedidos", String.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertTrue(getResponse.getBody() != null && getResponse.getBody().contains("\"textoOriginal\""));

        ResponseEntity<String> getByIdResponse = restTemplate
            .withBasicAuth("test", "test")
            .getForEntity(urlBase + "/pedido/" + id, String.class);

        assertEquals(HttpStatus.OK, getByIdResponse.getStatusCode());
        assertTrue(getByIdResponse.getBody() != null && getByIdResponse.getBody().contains("\"id\":" + id));
    }
}
