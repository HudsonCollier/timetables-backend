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

import java.util.List;

@Service
public class TripService {
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TrainService trainService;
    private final WebClient v2Client;

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

//        trip.setIntermediateStops(tripResponse.getIntermediateStops());
        trip.setIntermediateStops(stopEntities);
        return tripRepository.save(trip);
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

}













