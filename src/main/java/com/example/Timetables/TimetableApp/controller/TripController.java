package com.example.Timetables.TimetableApp.controller;

import com.example.Timetables.TimetableApp.dto.AddTripDto;
import com.example.Timetables.TimetableApp.model.Trip;
import com.example.Timetables.TimetableApp.service.TripService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST Controller used for searching for a trip, adding a trip, deleting a trip, and retrieving all of
 * the current users trips
 */
@RestController
@RequestMapping("/trips")
public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping("/add")
    public ResponseEntity<Trip> searchAndSaveTrip(@RequestBody AddTripDto request) {
        Trip savedTrip = tripService.searchAndSaveTrip(
                request.getDepartureStation(),
                request.getArrivalStation(),
                request.getTrainNumber());

        if (savedTrip.isLive()) {
            tripService.startLiveMonitoring(savedTrip.getId());
        }
        return ResponseEntity.ok(savedTrip);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Trip>> getUserTrips(@AuthenticationPrincipal UserDetails userDetails) {
        List<Trip> trips = tripService.getTripsForUser(userDetails.getUsername());
        return ResponseEntity.ok(trips);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrip(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        tripService.deleteTripForUser(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
