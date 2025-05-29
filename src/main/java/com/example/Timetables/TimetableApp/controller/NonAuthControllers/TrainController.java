package com.example.Timetables.TimetableApp.controller.NonAuthControllers;


import com.example.Timetables.TimetableApp.model.FullTripResponse;
import com.example.Timetables.TimetableApp.service.TrainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trains")
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @GetMapping
    public ResponseEntity<?> searchTrip(
            @RequestParam("fromStation") String fromStation,
            @RequestParam("toStation") String toStation,
            @RequestParam("trainNumber") long trainNumber
    ) {
        try {
            FullTripResponse trip = trainService.searchTrip(fromStation, toStation, trainNumber);

            if (trip == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No trip found for the provided train number and station codes.");
            }

            return ResponseEntity.ok(trip);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

}
