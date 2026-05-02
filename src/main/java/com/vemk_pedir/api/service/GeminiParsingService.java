package com.vemk_pedir.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GeminiParsingService {

    private static final Logger log = LoggerFactory.getLogger(GeminiParsingService.class);

    private static final String GEMINI_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";
    private static final int MAX_ATTEMPTS = 3;
    private static final long INITIAL_BACKOFF_MS = 15_000;

    private static final String PROMPT_TEMPLATE = """
        Extraia do texto de pedido abaixo os itens e a data de entrega.
        Retorne APENAS JSON válido, sem markdown, sem texto extra, no formato:
        {"itens": [{"produto": "nome do produto em minúsculas", "quantidade": NUMBER}], "data_entrega": "YYYY-MM-DD ou null"}
        Se não houver data, use null. Se não houver itens, use lista vazia.
        Texto: "%s"
        """;

    @Value("${gemini.api.key:}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();

    public Optional<IAParsingService.ParsedPedido> parse(String textoOriginal) {
        if (apiKey == null || apiKey.isBlank()) {
            return Optional.empty();
        }

        try {
            String prompt = String.format(PROMPT_TEMPLATE, textoOriginal.replace("\"", "\\\""));
            String requestBody = buildRequestBody(prompt);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GEMINI_URL + apiKey))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            HttpResponse<String> response = sendWithRetry(request);
            if (response == null) {
                return Optional.empty();
            }

            if (response.statusCode() != 200) {
                log.warn("Gemini retornou status {}, usando fallback regex", response.statusCode());
                return Optional.empty();
            }

            return parseGeminiResponse(response.body());

        } catch (Exception e) {
            log.warn("Erro ao chamar Gemini: {}, usando fallback regex", e.getMessage());
            return Optional.empty();
        }
    }

    private HttpResponse<String> sendWithRetry(HttpRequest request) {
        long backoff = INITIAL_BACKOFF_MS;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 429 || attempt == MAX_ATTEMPTS) {
                    return response;
                }

                log.warn("Gemini retornou 429 (tentativa {}/{}), novo retry em {}ms", attempt, MAX_ATTEMPTS, backoff);
                Thread.sleep(backoff);
                backoff *= 2;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Retry interrompido, usando fallback regex");
                return null;
            } catch (Exception e) {
                log.warn("Erro ao chamar Gemini: {}, usando fallback regex", e.getMessage());
                return null;
            }
        }

        return null;
    }

    private String buildRequestBody(String prompt) throws Exception {
        String escapedPrompt = objectMapper.writeValueAsString(prompt);
        return """
            {
              "contents": [{"parts": [{"text": %s}]}],
              "generationConfig": {"responseMimeType": "application/json"}
            }
            """.formatted(escapedPrompt);
    }

    private Optional<IAParsingService.ParsedPedido> parseGeminiResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String text = root.at("/candidates/0/content/parts/0/text").asText();

            JsonNode parsed = objectMapper.readTree(text);

            List<IAParsingService.ParsedItem> itens = new ArrayList<>();
            JsonNode itensNode = parsed.get("itens");
            if (itensNode != null && itensNode.isArray()) {
                for (JsonNode item : itensNode) {
                    String produto = item.get("produto").asText().trim();
                    int quantidade = item.get("quantidade").asInt();
                    if (!produto.isBlank() && quantidade > 0) {
                        itens.add(new IAParsingService.ParsedItem(produto, quantidade));
                    }
                }
            }

            LocalDate dataEntrega = null;
            JsonNode dataNode = parsed.get("data_entrega");
            if (dataNode != null && !dataNode.isNull()) {
                String dataStr = dataNode.asText();
                if (!dataStr.equals("null") && !dataStr.isBlank()) {
                    dataEntrega = LocalDate.parse(dataStr);
                }
            }

            log.info("Gemini parsing: {} itens extraídos", itens.size());
            return Optional.of(new IAParsingService.ParsedPedido(itens, dataEntrega));

        } catch (Exception e) {
            log.warn("Erro ao parsear resposta do Gemini: {}, usando fallback regex", e.getMessage());
            return Optional.empty();
        }
    }
}
