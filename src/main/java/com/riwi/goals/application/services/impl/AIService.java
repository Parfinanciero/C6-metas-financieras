package com.riwi.goals.application.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AIService {

    private final RestTemplate restTemplate;

    @Value("${huggingface.api.key}")
    private String apiKey;
    @Value("${huggingface.model.url}")
    private String modelUrl;

    public AIService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String generateAdvice(String prompt) {
        // Configurar headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Crear cuerpo de la solicitud
        String requestBody = String.format("""
        {
            "inputs": "%s",
            "parameters": {
                "max_length": 50,
                "temperature": 0.6,
                "top_p": 0.9
            }
        }
        """, prompt.replace("\"", "\\\""));

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            // Hacer la solicitud
            ResponseEntity<String> response = restTemplate.exchange(
                    modelUrl,
                    HttpMethod.POST,
                    request,
                    String.class
            );


            // Procesar respuesta
            return parseResponse(response.getBody());


        } catch (Exception e) {
            return "Error generando consejo: " + e.getMessage();
        }
    }

    private String parseResponse(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            // Extraer el texto generado
            String prompt = "";
            return root.get(0).get("generated_text").asText()
                    .replace(prompt, "") // Eliminar el prompt del resultado
                    .trim();

        } catch (Exception e) {
            return "Error procesando respuesta: " + e.getMessage();
        }
    }
}