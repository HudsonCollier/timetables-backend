package com.example.Timetables.TimetableApp.controller;

import com.example.Timetables.TimetableApp.model.Trip;
import com.example.Timetables.TimetableApp.service.TripService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tripsDB")
public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping("/search")
    public ResponseEntity<Trip> searchAndSaveTrip(
            @RequestParam String departingStation,
            @RequestParam String arrivalStation,
            @RequestParam long trainNumber
    ) {
        // Add this logging statement
        System.out.println("Received POST request to /tripsDB/search");
        System.out.println("Departing: " + departingStation + ", Arrival: " + arrivalStation + ", Train: " + trainNumber);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Trip trip = tripService.searchAndSaveTrip(departingStation, arrivalStation, trainNumber, email);
        if (trip == null) {
            System.out.println("TripService returned null.");
            return ResponseEntity.notFound().build();
        }
        System.out.println("TripService returned a trip object.");
        return ResponseEntity.ok(trip);
    }

    @GetMapping
    public ResponseEntity<List<Trip>> getUserTrips() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        List<Trip> trips = tripService.getUserTrips(email);
        return ResponseEntity.ok(trips);
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<?> deleteTrip(@PathVariable Long tripId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            tripService.deleteTrip(tripId, email);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



}
