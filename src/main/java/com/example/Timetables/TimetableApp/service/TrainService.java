package com.example.Timetables.TimetableApp.service;

import com.example.Timetables.TimetableApp.model.StopInfo;
import com.example.Timetables.TimetableApp.model.TripResponse;
import com.example.Timetables.TimetableApp.model.JourneyDetailsResponse.Stop;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TrainService {

    private final WebClient v2Client;
    private final ObjectMapper objectMapper;
    private static final double EARTH_RADIUS_KM = 6371.0;

    public TrainService(
            WebClient.Builder webClientBuilder,
            @Value("${ns.api.key}") String apiKey
    )
    {
        this.v2Client = webClientBuilder
                .baseUrl("https://gateway.apiportal.ns.nl/reisinformatie-api/api/v2")
                .defaultHeader("Ocp-Apim-Subscription-Key", apiKey)
                .build();

        this.objectMapper = new ObjectMapper();
    }

    public TripResponse searchTrip(String departureStationCode, String arrivalStationCode, long trainNumber)
    {
        com.example.Timetables.TimetableApp.model.JourneyDetailsResponse.TripResponse response = v2Client.get()
                .uri(uri -> uri
                        .path("/journey")
                        .queryParam("train", trainNumber)
                        .queryParam("departureStation", departureStationCode)
                        .queryParam("arrivalStation", arrivalStationCode)
                        .queryParam("omitCrowdForecast", false)
                        .build())
                .retrieve()
                .bodyToMono(com.example.Timetables.TimetableApp.model.JourneyDetailsResponse.TripResponse.class)
                .block();

        if (response == null || response.getPayload() == null || response.getPayload().getStops() == null) {
            return null;
        }

        List<Stop> allStops = response.getPayload().getStops();

        int departureIndex = -1;
        int arrivalIndex = -1;

        // Sets the departure and arrival stop indexes
        for (int i = 0; i < allStops.size(); i++) {
            String stationCode = allStops.get(i).getId().split("_")[0];
            if (stationCode.equalsIgnoreCase(departureStationCode) && departureIndex == -1) {
                departureIndex = i;
            }
            if (stationCode.equalsIgnoreCase(arrivalStationCode)) {
                arrivalIndex = i;
            }
        }

        if (departureIndex == -1 || arrivalIndex == -1 || departureIndex > arrivalIndex) {
            throw new IllegalArgumentException("Invalid station codes or stop sequence.");
        }

        Stop departureStop = allStops.get(departureIndex);
        Stop arrivalStop = allStops.get(arrivalIndex);

        // Find all the stops between the departure and arrival stops where the status is STOP
        List<Stop> relevantStops = allStops.subList(departureIndex + 1, arrivalIndex).stream()
                .filter(stop -> "STOP".equalsIgnoreCase(stop.getStatus()))
                .toList();


        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        List<StopInfo> stopInfos = relevantStops.stream().map(s -> {
            StopInfo info = new StopInfo();

            OffsetDateTime arrival = s.getStopArrivalInfo() != null ? s.getStopArrivalInfo().get(0).getStopArrivalTime() : null;
            OffsetDateTime departure = s.getStopDepartureInfo() != null ? s.getStopDepartureInfo().get(0).getStopDepartureTime() : null;

            info.setStationName(s.getStopLocation().getStopName());
            info.setStationCode(s.getId().split("_")[0]);

            info.setArrivalTime(arrival != null
                    ? arrival.atZoneSameInstant(ZoneId.of("Europe/Amsterdam")).format(timeFormatter)
                    : null);

            info.setDepartureTime(departure != null
                    ? departure.atZoneSameInstant(ZoneId.of("Europe/Amsterdam")).format(timeFormatter)
                    : null);

            info.setArrivalPlatform(s.getStopArrivalInfo() != null ? s.getStopArrivalInfo().get(0).getPlatformNumber() : null);
            info.setDeparturePlatform(s.getStopDepartureInfo() != null ? s.getStopDepartureInfo().get(0).getPlatformNumber() : null);
            info.setCancelled(s.getStopDepartureInfo() != null && s.getStopDepartureInfo().get(0).isCancelled());
            info.setDelayInSeconds(s.getStopDepartureInfo() != null ? s.getStopDepartureInfo().get(0).getDelayInSeconds() : 0);
            info.setStatus(s.getStatus());

            info.setLatitude(s.getStopLocation().getLatitude());
            info.setLongitude(s.getStopLocation().getLongitude());

            return info;
        }).toList();


        OffsetDateTime departureTime = departureStop.getStopDepartureInfo().get(0).getStopDepartureTime();
        OffsetDateTime arrivalTime = arrivalStop.getStopArrivalInfo().get(0).getStopArrivalTime();

        ZonedDateTime zonedDeparture = departureTime.atZoneSameInstant(ZoneId.of("Europe/Amsterdam"));
        ZonedDateTime zonedArrival = arrivalTime.atZoneSameInstant(ZoneId.of("Europe/Amsterdam"));

        DateTimeFormatter displayFormat = DateTimeFormatter.ofPattern("HH:mm");
        String formattedDepartureTime = zonedDeparture.format(displayFormat);
        String formattedArrivalTime = zonedArrival.format(displayFormat);


        int delay = departureStop.getStopDepartureInfo().get(0).getDelayInSeconds();
        boolean cancelled = departureStop.getStopDepartureInfo().get(0).isCancelled();
        boolean isOnTime = !cancelled && delay <= 60;
        boolean isDelayed = !cancelled && delay > 60;

        Duration timeUntilDep = Duration.between(ZonedDateTime.now(ZoneId.of("Europe/Amsterdam")), zonedDeparture);
        String timeUntilDeparture;
        if (timeUntilDep.toMinutes() > 0) {
            timeUntilDeparture = "Departing in " + timeUntilDep.toMinutes() + " min";
        } else if (timeUntilDep.toMinutes() == 0) {
            timeUntilDeparture = "Departing now";
        } else {
            timeUntilDeparture = "Departed";
        }


        // Finding distance between departure and arrival station
        double depLat = departureStop.getStopLocation().getLatitude();
        double depLon = departureStop.getStopLocation().getLongitude();
        double arrLat = arrivalStop.getStopLocation().getLatitude();
        double arrLon = arrivalStop.getStopLocation().getLongitude();
        double tripDistance = haversine(depLat, depLon, arrLat, arrLon);

        double roundedTripDistance = new BigDecimal(tripDistance)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        TripResponse trip = new TripResponse();
        trip.setTrainNumber((int) trainNumber);
        trip.setDepartureStation(departureStationCode);
        trip.setArrivalStation(arrivalStationCode);
        trip.setDirection(arrivalStop.getDirection());
        trip.setDepartureTime(formattedDepartureTime);
        trip.setArrivalTime(formattedArrivalTime);
        trip.setOnTime(isOnTime);
        trip.setDelayed(isDelayed);
        trip.setCancelled(cancelled);
        trip.setDelayDuration(delay);
        trip.setDeparturePlatformNumber(departureStop.getStopDepartureInfo().get(0).getPlatformNumber());
        trip.setArrivalPlatformNumber(arrivalStop.getStopArrivalInfo().get(0).getPlatformNumber());
        trip.setTimeUntilDeparture(timeUntilDeparture);
        trip.setDate(departureTime.toLocalDate());
        trip.setIntermediateStops(stopInfos);
        trip.setDepartureStationName(departureStop.getStopLocation().getStopName());
        trip.setArrivalStationName(arrivalStop.getStopLocation().getStopName());
        trip.setTripDistance(roundedTripDistance);

        return trip;
    }

    public double haversine(double depLat, double depLon, double arrLat, double arrLon) {
        double dLat = Math.toRadians(arrLat - depLat);
        double dLon = Math.toRadians(arrLon - depLon);

        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(Math.toRadians(depLat)) * Math.cos(Math.toRadians(arrLat))
                * Math.pow(Math.sin(dLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}