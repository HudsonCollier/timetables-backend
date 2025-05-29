package com.example.Timetables.TimetableApp.service;

import com.example.Timetables.TimetableApp.model.Station;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class StationService {

    private final WebClient stationClient;

    private static final String NS_API_URL = "https://gateway.apiportal.ns.nl/nsapp-stations/v2";

    public StationService(
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper,
            @Value("${ns.api.key}") String apiKey
    )
    {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("API key is missing. Please set it in application.properties.");
        }

        this.stationClient = webClientBuilder
                .baseUrl(NS_API_URL)
                .defaultHeader("Ocp-Apim-Subscription-Key", apiKey)
                .build();
    }

    public List<Station> getStations(String query)
    {
        JsonNode response = stationClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("q", query)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        List<Station> stations = new ArrayList<>();

        if (response != null && response.has("payload")) {
            JsonNode payload = response.get("payload");

            for (JsonNode stationNode : payload) {
                String code = stationNode.get("code").asText();
                String name = stationNode.get("namen").get("lang").asText();
                stations.add(new Station(name, code));
            }
        }

        System.out.println(stations);
        return stations;
    }
}
