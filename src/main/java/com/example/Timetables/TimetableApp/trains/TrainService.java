package com.example.Timetables.TimetableApp.trains;
import com.example.Timetables.TimetableApp.trains.models.TrainInfo;
import com.example.Timetables.TimetableApp.trains.models.TrainStop;
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

    public TrainInfo searchTrip(String departingStation, String arrivalStation, long trainNumber) {
        JsonNode journeyJson = v2Client.get()
                .uri(uri -> uri
                        .path("/journey")
                        .queryParam("train", trainNumber)
                        .queryParam("omitCrowdForecast", true)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (journeyJson == null || !journeyJson.has("payload")) {
            return null;
        }

        JsonNode stops = journeyJson.path("payload").path("stops");
        String direction = stops.get(0).path("destination").asText(null);
        List<TrainStop> trainStops = new ArrayList<>();

        for (JsonNode stop : stops) {
            String stationName = stop.path("stop").path("name").asText(null);
            String stopStatus = stop.path("stop").path("status").asText(null);

            // Arrival info for train at this stop
            // Arrival info for train at this stop
            JsonNode arrival = stop.path("arrivals").isEmpty() ? null : stop.path("arrivals").get(0);
            String arrivalTime = null;
            String arrivalPlatform = null;

            if (arrival != null) {
                arrivalTime = arrival.path("actualTime").asText(null);
                if (arrivalTime == null) {
                    arrivalTime = arrival.path("plannedTime").asText(null); // Fallback
                }
                arrivalPlatform = arrival.path("actualTrack").asText(null);
                if (arrivalPlatform == null) {
                    arrivalPlatform = arrival.path("plannedTrack").asText(null); // Fallback
                }
            }


            // Departure info for stop
            // Departure info for stop
            JsonNode departure = stop.path("departures").isEmpty() ? null : stop.path("departures").get(0);
            String departureTime = null;
            String departurePlatform = null;

            if (departure != null) {
                departureTime = departure.path("actualTime").asText(null);
                if (departureTime == null) {
                    departureTime = departure.path("plannedTime").asText(null); // Fallback
                }
                departurePlatform = departure.path("actualTrack").asText(null);
                if (departurePlatform == null) {
                    departurePlatform = departure.path("plannedTrack").asText(null); // Fallback
                }
            }


            boolean isCancelled = departure != null && departure.path("cancelled").asBoolean(false);
            int delay = departure != null ? departure.path("delayInSeconds").asInt(0) : 0;

            String stationId = stop.path("id").asText(); // "UT_0"
            String stationCode = stationId.split("_")[0]; // "UT_0" to "UT"

            TrainStop trainStop = new TrainStop();
            trainStop.setStationName(stationName);
            trainStop.setStatus(stopStatus);
            trainStop.setArrivalTime(arrivalTime);
            trainStop.setArrivalPlatform(arrivalPlatform);
            trainStop.setDepartureTime(departureTime);
            trainStop.setDeparturePlatform(departurePlatform);
            trainStop.setCancelled(isCancelled);
            trainStop.setDelayInSeconds(delay);
            trainStop.setStationCode(stationCode);

            trainStops.add(trainStop);
        }

        // Gets the info for the users departure station
        TrainStop departureStop = trainStops.stream()
                .filter(stop -> stop.getStationCode().equalsIgnoreCase(departingStation))
                .findFirst()
                .orElse(null);
        String departureTimeFromUsersDepStation = departureStop.getDepartureTime();
        ZonedDateTime zdt = ZonedDateTime.parse(departureTimeFromUsersDepStation, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        String formattedDepartureTime = zdt.format(DateTimeFormatter.ofPattern("HH:mm"));

        boolean isCancelled = departureStop.isCancelled();
        int delayInSeconds = departureStop.getDelayInSeconds();

        boolean isDelayed = false;
        boolean isOnTime = false;

        if (isCancelled) {
            isDelayed = false;
            isOnTime = false;
        } else if (delayInSeconds > 60) {
            isDelayed = true;
            isOnTime = false;
        } else {
            isDelayed = false;
            isOnTime = true;
        }


        String depPlatNum = departureStop.getDeparturePlatform();

        // Retrieves the info for the users arrival station
        TrainStop arrivalStop = trainStops.stream()
                .filter(stop -> stop.getStationCode().equalsIgnoreCase(arrivalStation))
                .findFirst()
                .orElse(null);
        String arrivalPlatNum = arrivalStop.getArrivalPlatform();
        String tripsArrivalTime = arrivalStop.getArrivalTime();
        ZonedDateTime zdt2 = ZonedDateTime.parse(tripsArrivalTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        String formattedArrivalTime = zdt2.format(DateTimeFormatter.ofPattern("HH:mm"));

        ZonedDateTime now = ZonedDateTime.now();
        Duration duration = Duration.between(now, zdt); // zdt is your parsed departure time
        long minutesUntilDeparture = duration.toMinutes();

        String timeUntilDeparture;
        if (minutesUntilDeparture > 0) {
            timeUntilDeparture = minutesUntilDeparture + " minutes";
        } else if (minutesUntilDeparture == 0) {
            timeUntilDeparture = "Departing now";
        } else {
            timeUntilDeparture = "Departed";
        }

        TrainInfo tripInfo = new TrainInfo();
        tripInfo.setTrainNumber(trainNumber);
        tripInfo.setDepartureStation(departingStation);
        tripInfo.setArrivalStation(arrivalStation);
        tripInfo.setDirection(direction);
        tripInfo.setDepartureTime(formattedDepartureTime);
        tripInfo.setArrivalTime(formattedArrivalTime);
        tripInfo.setOnTime(isOnTime);
        tripInfo.setDelayed(isDelayed);
        tripInfo.setCancelled(isCancelled);
        tripInfo.setDelayDuration(delayInSeconds);
        tripInfo.setDeparturePlatformNumber(depPlatNum);
        tripInfo.setArrivalPlatformNumber(arrivalPlatNum);
        tripInfo.setStops(trainStops);
        tripInfo.setTimeUntilDeparture(timeUntilDeparture);

        return tripInfo;
    }


}