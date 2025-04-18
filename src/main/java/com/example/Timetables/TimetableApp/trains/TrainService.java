package com.example.Timetables.TimetableApp.trains;
import com.example.Timetables.TimetableApp.trains.models.TrainInfo;
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
public class TrainService {

    private final WebClient v2Client;

    public TrainService(
            WebClient.Builder webClientBuilder,
            @Value("${ns.api.key}") String apiKey
    ) {
        this.v2Client = webClientBuilder
                .baseUrl("https://gateway.apiportal.ns.nl/reisinformatie-api/api/v2")
                .defaultHeader("Ocp-Apim-Subscription-Key", apiKey)
                .build();
    }

    public TrainInfo lookupByTrain(String fromStation, long trainNumber) {
        // 1) Fetch departures for fromStation
        JsonNode depJson = v2Client.get()
                .uri(uri -> uri
                        .path("/departures")
                        .queryParam("station", fromStation)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (depJson == null || !depJson.has("payload")) {
            return null;
        }

        // Find the departure entry for our train
        JsonNode departures = depJson.path("payload").path("departures");
        JsonNode depMatch = null;
        for (JsonNode node : departures) {
            if (node.path("product").path("number").asLong() == trainNumber) {
                depMatch = node;
                break;
            }
        }
        if (depMatch == null) { // Train not found at the departing station
            return null;
        }

        String toStation     = depMatch.path("direction").asText();
        String departureTime = depMatch.path("plannedDateTime").asText();
        int    depDelay      = depMatch.path("delayInMinutes").asInt(0);
        boolean depCancelled = depMatch.path("cancelled").asBoolean(false);
        int    depPlatform   = depMatch.path("plannedTrack").asInt(-1);

//        // Fetch arrivals for arrival station
//        JsonNode arrJson = v2Client.get()
//                .uri(uri -> uri
//                        .path("/arrivals")
//                        .queryParam("station", toStation)
//                        .build())
//                .retrieve()
//                .bodyToMono(JsonNode.class)
//                .block();
//
//        if (arrJson == null || !arrJson.has("payload")) {
//            return null;
//        }

        // 4) Find the arrival entry for our train
//        JsonNode arrivals = arrJson.path("payload").path("arrivals");
//        JsonNode arrMatch = null;
//        for (JsonNode node : arrivals) {
//            if (node.path("product").path("number").asLong() == trainNumber) {
//                arrMatch = node;
//                break;
//            }
//        }
//        if (arrMatch == null) {
//            return null; // arrival not found
//        }
//
//        String arrivalTime = arrMatch.path("plannedDateTime").asText();
//        int    arrDelay    = arrMatch.path("delayInMinutes").asInt(0);
//        boolean arrCancelled = arrMatch.path("cancelled").asBoolean(false);
//        int    arrPlatform = arrMatch.path("plannedTrack").asInt(-1);

        // 5) Build and return TrainInfo (no intermediate stops)
        TrainInfo info = new TrainInfo();
        info.setNumber(trainNumber);
        info.setDepartingStation(fromStation);
        info.setArrivalStation(toStation);
        info.setDepartureTime(departureTime);
//        info.setArrivalTime(arrivalTime);
//        info.setOnTime(depDelay == 0 && arrDelay == 0);
//        info.setDelayed(depDelay > 0 || arrDelay > 0);
//        info.setCancelled(depCancelled || arrCancelled);
        info.setPlatformNumber(depPlatform);
        // total delay (you could choose max(depDelay, arrDelay) or sum)
//        info.setDelayDuration(String.valueOf(Math.max(depDelay, arrDelay)));
        return info;
    }
}