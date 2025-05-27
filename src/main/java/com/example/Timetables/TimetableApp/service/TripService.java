package com.example.Timetables.TimetableApp.service;

import com.example.Timetables.TimetableApp.model.Trip;
import com.example.Timetables.TimetableApp.model.User;
// Removed unused import com.example.Timetables.TimetableApp.model.TrainInfo;
import com.example.Timetables.TimetableApp.model.TrainStop; // Keep this import
import com.example.Timetables.TimetableApp.repository.TripRepository;
import com.example.Timetables.TimetableApp.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode; // Keep this import
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient; // Keep this import

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class TripService {
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    // Reverted: Removed TrainService injection
    private final WebClient v2Client; // Reverted: Keep WebClient

    public TripService(
            TripRepository tripRepository,
            UserRepository userRepository,
            // Reverted: Removed TrainService from constructor
            WebClient.Builder webClientBuilder, // Reverted: Keep WebClientBuilder
            @Value("${ns.api.key}") String apiKey // Reverted: Keep Value for apiKey
    ) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        // Reverted: Removed TrainService assignment
        this.v2Client = webClientBuilder // Reverted: Keep WebClient initialization
                .baseUrl("https://gateway.apiportal.ns.nl/reisinformatie-api/api/v2")
                .defaultHeader("Ocp-Apim-Subscription-Key", apiKey)
                .build();
    }

    public Trip searchAndSaveTrip(String departingStation, String arrivalStation, long trainNumber, String email) {
        JsonNode journeyJson = v2Client.get()
                .uri(uri -> uri
                        .path("/journey")
                        .queryParam("train", trainNumber)
                        .queryParam("omitCrowdForecast", true)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        // Added Logging
        System.out.println("NS API Response: " + journeyJson);

        if (journeyJson == null || !journeyJson.has("payload")) {
            System.out.println("NS API response is null or missing payload for train: " + trainNumber);
            return null;
        }

        JsonNode stopsJson = journeyJson.path("payload").path("stops");
        // Added Logging
        System.out.println("Stops from NS API: " + stopsJson);

        List<TrainStop> trainStops = new ArrayList<>();
        TrainStop departureStop = null;
        TrainStop arrivalStop = null;

        DateTimeFormatter apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

        for (JsonNode stop : stopsJson) {
            String stationName = stop.path("stop").path("name").asText(null);
            String stationId = stop.path("id").asText(null); // Use 'id' for code and split
            String stationCode = null;
            if (stationId != null) {
                stationCode = stationId.split("_")[0]; // Extract station code
            }
            String stopStatus = stop.path("stop").path("status").asText(null);

            // Arrival info for train at this stop
            JsonNode arrivalJson = stop.path("arrivals").isEmpty() ? null : stop.path("arrivals").get(0);
            String arrivalTimeStr = null;
            String arrivalPlatform = null;
            Integer arrivalDelay = null;


            if (arrivalJson != null) {
                arrivalTimeStr = arrivalJson.path("actualTime").asText(null);
                if (arrivalTimeStr == null) {
                    arrivalTimeStr = arrivalJson.path("plannedTime").asText(null); // Fallback
                }
                arrivalPlatform = arrivalJson.path("actualTrack").asText(null);
                if (arrivalPlatform == null) {
                    arrivalPlatform = arrivalJson.path("plannedTrack").asText(null); // Fallback
                }
                arrivalDelay = arrivalJson.path("delayInSeconds").asInt(0); // Get delay


            }


            // Departure info for stop
            JsonNode departureJson = stop.path("departures").isEmpty() ? null : stop.path("departures").get(0);
            String departureTimeStr = null;
            String departurePlatform = null;
            boolean isCancelled = false;
            Integer departureDelay = null;


            if (departureJson != null) {
                departureTimeStr = departureJson.path("actualTime").asText(null);
                if (departureTimeStr == null) {
                    departureTimeStr = departureJson.path("plannedTime").asText(null); // Fallback
                }
                departurePlatform = departureJson.path("actualTrack").asText(null);
                if (departurePlatform == null) {
                    departurePlatform = departureJson.path("plannedTrack").asText(null); // Fallback
                }
                isCancelled = departureJson.path("cancelled").asBoolean(false);
                departureDelay = departureJson.path("delayInSeconds").asInt(0); // Get delay

            }

            // Determine delay for the stop - use departure delay if available, otherwise arrival delay
            Integer delayInSeconds = (departureDelay != null && departureDelay > 0) ? departureDelay : ((arrivalDelay != null && arrivalDelay > 0) ? arrivalDelay : 0);
            boolean stopCancelled = isCancelled; // Use departure cancellation status

            TrainStop trainStop = new TrainStop();
            trainStop.setStationName(stationName);
            trainStop.setStationCode(stationCode);
            trainStop.setArrivalTime(arrivalTimeStr); // Store raw time string
            trainStop.setDepartureTime(departureTimeStr); // Store raw time string
            trainStop.setCancelled(stopCancelled);
            trainStop.setArrivalPlatform(arrivalPlatform);
            trainStop.setDeparturePlatform(departurePlatform);
            trainStop.setDelayInSeconds(delayInSeconds);
            trainStop.setStatus(stopStatus);

            trainStops.add(trainStop);

            // Identify departure and arrival stops based on provided station codes
            if (stationCode != null) {
                if (stationCode.equalsIgnoreCase(departingStation)) {
                    departureStop = trainStop;
                    // Added Logging
                    System.out.println("Identified departure stop: " + departingStation);
                }
                if (stationCode.equalsIgnoreCase(arrivalStation)) {
                    arrivalStop = trainStop;
                    // Added Logging
                    System.out.println("Identified arrival stop: " + arrivalStation);
                }
            }
        }

        if (departureStop == null || arrivalStop == null) {
            // Added Logging
            System.out.println("Departure stop (" + departingStation + ") or arrival stop (" + arrivalStation + ") not found in journey stops.");
            return null; // This should now correctly return 404 from the controller
        }

        // Now populate the Trip entity using data from identified departureStop and arrivalStop
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Trip trip = new Trip();
        trip.setUser(user);
        trip.setTrainNumber(String.valueOf(trainNumber)); // Use the requested train number

        // Use station codes for departure and arrival stations in Trip entity
        trip.setDepartureStation(departureStop.getStationCode());
        trip.setArrivalStation(arrivalStop.getStationCode());

        // Determine overall trip direction (using the first stop's destination as before)
        String direction = stopsJson.get(0).path("destination").asText(null);
        trip.setDirection(direction);


        // Convert time strings from TrainStop back to LocalDateTime
        LocalDateTime departureDateTime = null;
        if (departureStop.getDepartureTime() != null) {
            try {
                // NS API time format: "yyyy-MM-dd'T'HH:mm:ssZ"
                // Need to use ZonedDateTime and convert to LocalDateTime
                departureDateTime = ZonedDateTime.parse(departureStop.getDepartureTime(), apiFormatter).toLocalDateTime();
            } catch (Exception e) {
                System.err.println("Error parsing departure time: " + departureStop.getDepartureTime() + " - " + e.getMessage());
                // Handle parsing error, maybe log it and leave as null or throw
            }
        }
        trip.setDepartureTime(departureDateTime);


        LocalDateTime arrivalDateTime = null;
        if (arrivalStop.getArrivalTime() != null) {
            try {
                arrivalDateTime = ZonedDateTime.parse(arrivalStop.getArrivalTime(), apiFormatter).toLocalDateTime();
            } catch (Exception e) {
                System.err.println("Error parsing arrival time: " + arrivalStop.getArrivalTime() + " - " + e.getMessage());
                // Handle parsing error
            }
        }
        trip.setArrivalTime(arrivalDateTime);

        // Determine overall trip status (cancelled, delayed, on time)
        boolean isCancelled = departureStop.isCancelled();
        int delayInSeconds = (departureStop.getDelayInSeconds() != null) ? departureStop.getDelayInSeconds() : 0;
        boolean isDelayed = !isCancelled && delayInSeconds > 60;
        boolean isOnTime = !isCancelled && !isDelayed;

        trip.setOnTime(isOnTime);
        trip.setDelayed(isDelayed);
        trip.setCancelled(isCancelled);
        trip.setDelayDuration(delayInSeconds); // Use delay from departure stop


        trip.setDeparturePlatformNumber(departureStop.getDeparturePlatform());
        trip.setArrivalPlatformNumber(arrivalStop.getArrivalPlatform());

        // Calculate time until departure
        Integer timeUntilDepartureMinutes = null;
        if (departureDateTime != null) {
            ZonedDateTime now = ZonedDateTime.now();
            if (departureStop.getDepartureTime() != null) {
                try {
                    ZonedDateTime departureZonedTime = ZonedDateTime.parse(departureStop.getDepartureTime(), apiFormatter);
                    Duration duration = Duration.between(now, departureZonedTime);
                    timeUntilDepartureMinutes = (int) duration.toMinutes();
                } catch (Exception e) {
                    System.err.println("Error calculating time until departure: " + e.getMessage());
                    // Handle error
                }
            }
        }
        trip.setTimeUntilDeparture(timeUntilDepartureMinutes);

        trip.setDate(LocalDateTime.now()); // Keep setting the trip date to now


        Trip savedTrip = tripRepository.save(trip);
        // Added Logging
        System.out.println("Saved Trip object: " + savedTrip);

        return savedTrip;
    }

    public List<Trip> getUserTrips(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return tripRepository.findByUserId(user.getId());
    }

    @Transactional
    public void deleteTrip(Long tripId, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (!trip.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("User not authorized to delete this trip");
        }

        tripRepository.delete(trip);
    }
}