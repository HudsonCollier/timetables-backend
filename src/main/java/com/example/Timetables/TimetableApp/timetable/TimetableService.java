package com.example.Timetables.TimetableApp.timetable;
import com.example.Timetables.TimetableApp.stations.Station;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class TimetableService {
    private final WebClient timetableClient;

    private static final String NS_DEP_API_URL ="https://gateway.apiportal.ns.nl/reisinformatie-api/api/v2";

    public TimetableService(
            WebClient.Builder webClientBuilder,
            @Value("${ns.api.key}") String apiKey
    ) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("API key is missing. Please set it in application.properties.");
        }

        this.timetableClient = webClientBuilder
                .baseUrl(NS_DEP_API_URL)
                .defaultHeader("Ocp-Apim-Subscription-Key", apiKey)
                .build();
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


    public List<TimetableEntry> getTimetable(String stationCode) {
        JsonNode response = timetableClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/departures")
                        .queryParam("station", stationCode)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        List<TimetableEntry> timetable = new ArrayList<>();

        if (response != null && response.has("payload") && response.get("payload").has("departures")) {
            JsonNode departures = response.get("payload").get("departures");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
            ZonedDateTime now = ZonedDateTime.now();

            for (JsonNode dep : departures) {
                String plannedTimeStr = dep.get("plannedDateTime").asText();
                ZonedDateTime departureTime = ZonedDateTime.parse(plannedTimeStr, formatter);
                Duration duration = Duration.between(now, departureTime);
                long minutesUntilDeparture = duration.toMinutes();

                if (minutesUntilDeparture >= 0 && minutesUntilDeparture <= 120) {
                    String direction = dep.get("direction").asText();
                    String trainNumber = dep.get("product").get("number").asText();
                    String platform = dep.has("plannedTrack") ? dep.get("plannedTrack").asText() : "TBD";

                    // Get intermediate stations
                    List<String> intermediateStations = new ArrayList<>();
                    if (dep.has("routeStations")) {
                        for (JsonNode stationNode : dep.get("routeStations")) {
                            intermediateStations.add(stationNode.get("mediumName").asText());
                        }
                    }

                    TimetableEntry entry = new TimetableEntry(
                            departureTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                            direction,
                            trainNumber,
                            platform,
                            intermediateStations
                    );

                    timetable.add(entry);
                }
            }
        }
        return timetable;
    }
}







