package com.example.Timetables.TimetableApp.Stations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Service
public class StationService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${ns.api.key}")
    private String apiKey;

    private static final String NS_API_URL = "https://gateway.apiportal.ns.nl/reisinformatie-api/api/v2/departures";
    public StationService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper,
                          @Value("${ns.api.key}") String apiKey) {
        this.webClient = webClientBuilder
                .baseUrl(NS_API_URL)
                .defaultHeader("Ocp-Apim-Subscription-Key", apiKey)
                .build();
        this.objectMapper = objectMapper;
    }

    public JsonNode getDepartures(String stationCode) {
        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("station", stationCode).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            return objectMapper.readTree(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse departures JSON", e);
        }

    }
}
