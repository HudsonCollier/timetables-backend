package com.example.Timetables.TimetableApp.service;

import com.example.Timetables.TimetableApp.model.JourneyDetailsResponse.Stop;
import com.example.Timetables.TimetableApp.model.StopEntity;
import com.example.Timetables.TimetableApp.model.TripResponse;
import com.example.Timetables.TimetableApp.model.Trip;
import com.example.Timetables.TimetableApp.model.User;
// Removed unused import com.example.Timetables.TimetableApp.model.TrainInfo;
import com.example.Timetables.TimetableApp.repository.TripRepository;
import com.example.Timetables.TimetableApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TripService {
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TrainService trainService;
    private final WebClient v2Client;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public TripService(
            TripRepository tripRepository,
            UserRepository userRepository,
            TrainService trainService,

            WebClient.Builder webClientBuilder,
            @Value("${ns.api.key}") String apiKey
    ) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.trainService = trainService;
        this.v2Client = webClientBuilder
                .baseUrl("https://gateway.apiportal.ns.nl/reisinformatie-api/api/v2")
                .defaultHeader("Ocp-Apim-Subscription-Key", apiKey)
                .build();
    }

    public Trip searchAndSaveTrip(String departureStation, String arrivalStation, long trainNumber) {
        String username = getAuthenticatedUsername();
        System.out.println(username + "USERNAME");
        TripResponse tripResponse = trainService.searchTrip(departureStation, arrivalStation, trainNumber);

        if(tripResponse == null) {
            throw new RuntimeException("Trip response not received");
        }

        //Find the user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Trip trip = new Trip();
        trip.setUser(user);
        trip.setTrainNumber(trainNumber);
        trip.setDepartureStation(tripResponse.getDepartureStation());
        trip.setArrivalStation(tripResponse.getArrivalStation());
        trip.setDirection(tripResponse.getDirection());
        trip.setDepartureTime(tripResponse.getDepartureTime());
        trip.setArrivalTime(tripResponse.getArrivalTime());
        trip.setOnTime(tripResponse.isOnTime());
        trip.setDelayed(tripResponse.isDelayed());
        trip.setCancelled(tripResponse.isCancelled());
        trip.setDelayDuration(tripResponse.getDelayDuration());
        trip.setDeparturePlatformNumber(tripResponse.getDeparturePlatformNumber());
        trip.setArrivalPlatformNumber(tripResponse.getArrivalPlatformNumber());
        trip.setTimeUntilDeparture(tripResponse.getTimeUntilDeparture());
        trip.setDate(tripResponse.getDate());
        trip.setTripDistance(tripResponse.getTripDistance());
        trip.setTripDuration(tripResponse.getTripDuration());

        List<StopEntity> stopEntities = tripResponse.getIntermediateStops().stream().map(stopInfo -> {
            StopEntity stop = new StopEntity();

            stop.setStationName(stopInfo.getStationName());
            stop.setStationCode(stopInfo.getStationCode());
            stop.setArrivalTime(stopInfo.getArrivalTime());
            stop.setDepartureTime(stopInfo.getDepartureTime());
            stop.setArrivalPlatform(stopInfo.getArrivalPlatform());
            stop.setDeparturePlatform(stopInfo.getDeparturePlatform());
            stop.setTrip(trip);
            return stop;
        }).toList();

        trip.setIntermediateStops(stopEntities);

        trip.setDepartureStationName(tripResponse.getDepartureStationName());
        trip.setArrivalStationName(tripResponse.getArrivalStationName());

        // Checking if the trip is live
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime currentTime = LocalTime.now(ZoneId.of("Europe/Amsterdam"));
        LocalTime arrivalTime = LocalTime.parse(tripResponse.getArrivalTime(), timeFormatter);

        boolean isLive = !tripResponse.isCancelled() && currentTime.isBefore(arrivalTime);
        trip.setLive(isLive);

        Trip savedTrip = tripRepository.save(trip);
        if (isLive) {
//            startLiveMonitoring(savedTrip.getId());
        }

        return savedTrip;
    }

    private String getAuthenticatedUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    public List<Trip> getTripsForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("MADE IT IN GETTRIPS");
        return tripRepository.findByUserId(user.getId());
    }

    public void deleteTripForUser(Long tripId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (!trip.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to delete this trip");
        }

        tripRepository.delete(trip);
    }












//    // LIVE DATA
//    public void startLiveMonitoring(long tripId) {
//        scheduler.scheduleAtFixedRate(() -> {
//            try {
//                updateLiveTrip(tripId);
//            } catch (Exception e) {
//                // ERROR
//            }
//        }, 0, 30, TimeUnit.SECONDS);
//    }
//
//    public void updateLiveTrip(Long tripId) {
//        Trip trip = tripRepository.findById(tripId)
//                .orElseThrow(() -> new RuntimeException("Trip not found"));
//
//        TripResponse liveTripData = trainService.searchTrip(
//                trip.getDepartureStation(),
//                trip.getArrivalStation(),
//                trip.getTrainNumber()
//        );
//
//        if (liveTripData == null) {
//            return;
//        }
//
//        // Update trip data
//        trip.setOnTime(liveTripData.isOnTime());
//        trip.setDelayed(liveTripData.isDelayed());
//        trip.setCancelled(liveTripData.isCancelled());
//        trip.setDelayDuration(liveTripData.getDelayDuration());
//        trip.setDeparturePlatformNumber(liveTripData.getDeparturePlatformNumber());
//        trip.setArrivalPlatformNumber(liveTripData.getArrivalPlatformNumber());
//        trip.setTimeUntilDeparture(liveTripData.getTimeUntilDeparture());
//
//        // Update intermediate stops
//        List<StopEntity> updatedStops = liveTripData.getIntermediateStops().stream()
//                .map(stopInfo -> {
//                    StopEntity stop = new StopEntity();
//                    stop.setStationName(stopInfo.getStationName());
//                    stop.setStationCode(stopInfo.getStationCode());
//                    stop.setArrivalTime(stopInfo.getArrivalTime());
//                    stop.setDepartureTime(stopInfo.getDepartureTime());
//                    stop.setArrivalPlatform(stopInfo.getArrivalPlatform());
//                    stop.setDeparturePlatform(stopInfo.getDeparturePlatform());
//                    stop.setTrip(trip);
//                    return stop;
//                }).toList();
//
//        trip.setIntermediateStops(updatedStops);
//
//        // Check if the trip is still live by comparing the time strings
//        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
//        LocalTime currentTime = LocalTime.now(ZoneId.of("Europe/Amsterdam"));
//        LocalTime arrivalTime = LocalTime.parse(trip.getArrivalTime(), timeFormatter);
//
//        boolean isStillLive = !trip.isCancelled() && currentTime.isBefore(arrivalTime);
//        trip.setLive(isStillLive);
//
//        tripRepository.save(trip);
//    }

}













