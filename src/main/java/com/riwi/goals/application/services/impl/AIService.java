package com.riwi.goals.application.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riwi.goals.application.dtos.response.GenerationResult;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {
    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    @Value("${ia.api.key}")
    private String apiKey;

    @Value("${ia.api.url}")
    private String apiUrl;

    @Value("${ia.model}")
    private String model;

    @Value("${ia.temperature}")
    private double temperature;

    @Value("${ia.max_tokens}")
    private int maxTokens;

    @Value("${ia.top_p}")
    private double topP;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GenerationResult generateAdvice(String prompt) {
        try {
            HttpEntity<String> request = buildRequest(prompt);
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            return parseResponse(response.getBody());

        } catch (HttpClientErrorException e) {
            return handleHttpError(e.getStatusCode(), e.getResponseBodyAsString());
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON: {}", e.getMessage());
            return GenerationResult.builder()
                    .error(true)
                    .errorMessage("Error processing API response")
                    .build();
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            return GenerationResult.builder()
                    .error(true)
                    .errorMessage("Internal server error")
                    .build();
        }
    }

    private HttpEntity<String> buildRequest(String prompt) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "max_tokens", maxTokens,
                "mode", "java",
                "model", model,
                "n", 1,
                "temperature", temperature,
                "text", prompt
        );

        return new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
    }

    private GenerationResult parseResponse(String jsonResponse) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(jsonResponse);

        // Validar que la respuesta tiene el formato esperado
        if (!root.has("data") || !root.path("data").has("outputs")) {
            throw new JsonProcessingException("Invalid response format from IA API") {};
        }

        JsonNode outputs = root.path("data").path("outputs");
        if (!outputs.isArray() || outputs.isEmpty()) {
            throw new JsonProcessingException("No outputs found in IA response") {};
        }

        // Extraer el texto generado
        String content = outputs.get(0).path("text").asText();

        return GenerationResult.builder()
                .content(content)
                .build();
    }


    private GenerationResult handleHttpError(HttpStatusCode statusCode, String responseBody) {
        HttpStatus status = HttpStatus.valueOf(statusCode.value());

        String errorMessage = switch (status) {
            case BAD_REQUEST -> "Invalid request format";
            case UNAUTHORIZED -> "Invalid API key";
            case PAYMENT_REQUIRED -> "Insufficient balance";
            case UNPROCESSABLE_ENTITY -> "Invalid parameters";
            case TOO_MANY_REQUESTS -> "Rate limit exceeded";
            case INTERNAL_SERVER_ERROR -> "IA server error";
            case SERVICE_UNAVAILABLE -> "Service overloaded";
            default -> "Unknown error: " + status;
        };

        logger.error("IA API Error [{}]: {}", status.value(), errorMessage);
        logger.error("Response body: {}", responseBody);
        return GenerationResult.builder()
                .error(true)
                .errorMessage(errorMessage)
                .build();
    }
}