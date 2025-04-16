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

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private static final String NS_API_URL = "https://gateway.apiportal.ns.nl/nsapp-stations/v2";

    public StationService(
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper,
            @Value("${ns.api.key}") String apiKey
    ) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("API key is missing. Please set it in application.properties.");
        }

        this.webClient = webClientBuilder
                .baseUrl(NS_API_URL)
                .defaultHeader("Ocp-Apim-Subscription-Key", apiKey)
                .build();
        this.objectMapper = objectMapper;
    }

    public List<String> getStations(String query) {
//        List<String> stations = new ArrayList<String>();
        String url = NS_API_URL + "?q=" + query;
        JsonNode response = webClient.get()
                .uri(url)
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
}
