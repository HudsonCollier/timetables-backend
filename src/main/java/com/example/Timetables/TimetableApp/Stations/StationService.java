package com.example.Timetables.TimetableApp.Stations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final WebClient stationClient;
    private final WebClient timetableClient;
    private final ObjectMapper objectMapper;

    private static final String NS_API_URL = "https://gateway.apiportal.ns.nl/nsapp-stations/v2";
    private static final String NS_DEP_API_URL ="https://gateway.apiportal.ns.nl/reisinformatie-api/api/v2";

    public StationService(
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper,
            @Value("${ns.api.key}") String apiKey
    ) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("API key is missing. Please set it in application.properties.");
        }

        this.stationClient = webClientBuilder
                .baseUrl(NS_API_URL)
                .defaultHeader("Ocp-Apim-Subscription-Key", apiKey)
                .build();

        this.timetableClient = webClientBuilder
                .baseUrl(NS_DEP_API_URL)
                .defaultHeader("Ocp-Apim-Subscription-Key", apiKey)
                .build();
        this.objectMapper = objectMapper;
    }

    public List<String> getStations(String query) {
        JsonNode response = stationClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("q", query)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (response != null && response.has("payload")) {
            List<JsonNode> stationNodes = response.get("payload").findValues("namen");

            return stationNodes.stream()
                    .map(namenNode -> namenNode.get("lang").asText())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<String> getTimetableForStation(String stationCode) {
        JsonNode response = timetableClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/departures")
                        .queryParam("station", stationCode)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        List<String> timetable = new ArrayList<>();

        if (response != null && response.has("payload") && response.get("payload").has("departures")) {
            JsonNode departures = response.get("payload").get("departures");

            for (JsonNode dep : departures) {
                String destination = dep.get("direction").asText();
                String trainNumber = dep.get("product").get("number").asText();
                String time = dep.get("plannedDateTime").asText();

                String entry = time + " – Train " + trainNumber + " to " + destination;
                timetable.add(entry);
            }
        }
        return timetable;
    }
}
