package com.example.Timetables.TimetableApp.service;

import com.example.Timetables.TimetableApp.model.StopEntity;
import com.example.Timetables.TimetableApp.model.TripResponse;
import com.example.Timetables.TimetableApp.model.Trip;
import com.example.Timetables.TimetableApp.model.User;
import com.example.Timetables.TimetableApp.repository.PassportRepository;
import com.example.Timetables.TimetableApp.repository.TripRepository;
import com.example.Timetables.TimetableApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Service that is responsible for handling the logic behind a user searching for a trip
 */
@Service
public class TripService {
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TrainService trainService;
    private final WebClient v2Client;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<Long, ScheduledFuture<?>> activeMonitors = new ConcurrentHashMap<>();
    private final PassportRepository passportRepository;
    private final PassportService passportService;

    public TripService(
            TripRepository tripRepository,
            UserRepository userRepository,
            TrainService trainService,
            PassportService passportService,
            PassportRepository passportRepository,

            WebClient.Builder webClientBuilder,
            @Value("${ns.api.key}") String apiKey
    ) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.trainService = trainService;
        this.passportService = passportService;
        this.passportRepository = passportRepository;
        this.v2Client = webClientBuilder
                .baseUrl("https://gateway.apiportal.ns.nl/reisinformatie-api/api/v2")
                .defaultHeader("Ocp-Apim-Subscription-Key", apiKey)
                .build();
    }

    /**
     * Allows a user to search for a trip, i.e., Amsterdam Centraal -> Utrecht Centraal on Train 215, and retrieves all
     * of the data from the journey, such as departing and arriving times, departing and arriving platforms, status of the
     * trips, delay info, and more.
     * @param departureStation - The station code of the departure station
     * @param arrivalStation - The station code of the arrival station
     * @param trainNumber - THe train number
     * @return - THe JSON for the trip
     */
    public Trip searchAndSaveTrip(String departureStation, String arrivalStation, long trainNumber) {
        String username = getAuthenticatedUsername();
        System.out.println(username + "USERNAME");
        TripResponse tripResponse = trainService.searchTrip(departureStation, arrivalStation, trainNumber);

        if(tripResponse == null) {
            throw new RuntimeException("Trip response not received");
        }

        //Finds the current user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Sets the trip entity with the data retrieved from the trip response
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
        trip.setDepartureCity(extractCityName(tripResponse.getDepartureStationName()));
        trip.setArrivalCity(extractCityName(tripResponse.getArrivalStationName()));

        // Handles the arrival times
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        ZoneId zone = ZoneId.of("Europe/Amsterdam");

        ZonedDateTime now = ZonedDateTime.now(zone);
        LocalDateTime arrivalLocalDateTime = LocalDateTime.parse(
                trip.getDate() + " " + trip.getArrivalTime(), formatter
        );
        ZonedDateTime arrivalDateTime = arrivalLocalDateTime.atZone(zone);

        long arrivingInMinutes = Duration.between(now, arrivalDateTime).toMinutes();
        if (arrivingInMinutes <= 0) {
            trip.setTimeUntilArrival("Arrived");
        } else {
            trip.setTimeUntilArrival(arrivingInMinutes + " min");
        }

        boolean isStillLive = !trip.isCancelled() && now.isBefore(arrivalDateTime);
        trip.setLive(isStillLive);

        Trip savedTrip = tripRepository.save(trip);
        if (isStillLive) {
            startLiveMonitoring(savedTrip.getId());
        }

        passportService.updatePassport(savedTrip);
        return savedTrip;
    }

    /**
     * Gets the username for the authenticated user
     */
    private String getAuthenticatedUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    /**
     * Gets all of the trips for the currently authenticate user
     */
    public List<Trip> getTripsForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return tripRepository.findByUserId(user.getId());
    }

    /**
     * Handles the logic for deleting a trip from a users account
     */
    public void deleteTripForUser(Long tripId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (!trip.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to delete this trip");
        }

        passportService.handleTripDeletion(trip);

        tripRepository.delete(trip);
    }


    // -------- LIVE TRIP METHODS -----

    /**
     * If a trip is marked as live, i.e. the train has not arrived at the arrival stop yet. We monitor this trip
     * and re-fetch the data every minute looking for updated delay and status info
     */
    public void startLiveMonitoring(long tripId) {
        if (activeMonitors.containsKey(tripId)) {
            return;
        }

        ScheduledFuture<?> monitor = scheduler.scheduleAtFixedRate(() -> {
            try {
                updateLiveTrip(tripId);
            } catch (Exception e) {
                System.err.println("Error updating live trip " + tripId + ": " + e.getMessage());
                stopLiveMonitoring(tripId);
            }
        }, 0, 60, TimeUnit.SECONDS);

        activeMonitors.put(tripId, monitor);
    }

    /**
     * Once a train has made it to the arrival stop, we stop re-fetching the trip data every minute
     */
    public void stopLiveMonitoring(long tripId) {
        ScheduledFuture<?> monitor = activeMonitors.remove(tripId);
        if (monitor != null) {
            monitor.cancel(false);
        }
    }

    /**
     * Updates the trip data for a given trip, if that trip is marked as live
     */
    public void updateLiveTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        TripResponse liveTripData = trainService.searchTrip(
                trip.getDepartureStation(),
                trip.getArrivalStation(),
                trip.getTrainNumber()
        );

        if (liveTripData == null) {
            stopLiveMonitoring(tripId);
            return;
        }

        trip.setOnTime(liveTripData.isOnTime());
        trip.setDelayed(liveTripData.isDelayed());
        trip.setCancelled(liveTripData.isCancelled());
        trip.setDelayDuration(liveTripData.getDelayDuration());
        trip.setDeparturePlatformNumber(liveTripData.getDeparturePlatformNumber());
        trip.setArrivalPlatformNumber(liveTripData.getArrivalPlatformNumber());
        trip.setTimeUntilDeparture(liveTripData.getTimeUntilDeparture());

        List<StopEntity> updatedStops = liveTripData.getIntermediateStops().stream()
                .map(stopInfo -> {
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

        trip.setIntermediateStops(updatedStops);

        // Check if the trip is still live
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        ZoneId zone = ZoneId.of("Europe/Amsterdam");

        ZonedDateTime now = ZonedDateTime.now(zone);

        LocalDateTime arrivalLocalDateTime = LocalDateTime.parse(
                trip.getDate() + " " + trip.getArrivalTime(), formatter
        );
        ZonedDateTime arrivalDateTime = arrivalLocalDateTime.atZone(zone);

        long arrivingInMinutes = Duration.between(now, arrivalDateTime).toMinutes();
        if (arrivingInMinutes <= 0) {
            trip.setTimeUntilArrival("Arrived");
        } else {
            trip.setTimeUntilArrival(arrivingInMinutes + " min");
        }

        boolean isStillLive = !trip.isCancelled() && now.isBefore(arrivalDateTime);

        trip.setLive(isStillLive);

        // If trip is no longer live, stop monitoring
        if (!isStillLive) {
            stopLiveMonitoring(tripId);
        }

        tripRepository.save(trip);
        passportService.updatePassport(trip);
    }

    /**
     * Helper method used in order to extract the city name out of a station name using common dutch
     * suffixes
     */
    public static String extractCityName(String stationName) {
        String[] knownSuffixes = {
                "Centraal", "Zuid", "Noord", "CS", "Laan v NOI", "Buiten", "De Vink", "Driebergen-Zeist"
        };
        for (String suffix : knownSuffixes) {
            if (stationName.endsWith(" " + suffix)) {
                return stationName.substring(0, stationName.length() - suffix.length()).trim();
            }
        }
        return stationName;
    }
}













