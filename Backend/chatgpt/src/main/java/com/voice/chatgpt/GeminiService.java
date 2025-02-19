package com.voice.chatgpt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class GeminiService {

    private static final Logger logger = Logger.getLogger(GeminiService.class.getName());

    private final WebClient webClient;

    @Value("${gemini.api.key}") // Read API Key from properties
    private String apiKey;

    public GeminiService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent")
                .build();
    }

    public Mono<Map<String, Object>> getChatResponse(String userMessage) {
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", userMessage)
                        })
                }
        );

        logger.info("Sending request to Gemini API: " + requestBody);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .doOnEach(signal -> {
                    if (signal.getType() == SignalType.ON_NEXT) {
                        logger.info("Received Response: " + signal.get());
                    } else if (signal.getType() == SignalType.ON_ERROR) {
                        logger.log(Level.SEVERE, "Error calling Gemini API", signal.getThrowable());
                    }
                });
    }
}