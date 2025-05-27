package com.example.Timetables.TimetableApp.controller;


import com.example.Timetables.TimetableApp.model.TrainInfo;
import com.example.Timetables.TimetableApp.service.TrainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trains")
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    /**
     * Lookup a train’s departure info by its number, from a given station.
     * Example: GET /trains?fromStation=UT&trainNumber=8704
     */
    @GetMapping
    public ResponseEntity<TrainInfo> getByTrain(
            @RequestParam("fromStation") String fromStation,
            @RequestParam("toStation") String toStation,
            @RequestParam("trainNumber") long trainNumber
    ) {
        TrainInfo info = trainService.searchTrip(fromStation, toStation, trainNumber);
        if (info == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(info);
    }







    @GetMapping("/test")
    public ResponseEntity<TrainInfo> testTrainInfoResponse() {
        TrainInfo info = new TrainInfo();
        info.setTrainNumber(1234);
        info.setDepartureStation("UT");
        info.setArrivalStation("ASD");
        info.setDirection("North");
        info.setDepartureTime("12:00");
        info.setArrivalTime("13:30");
        info.setOnTime(true);
        info.setDelayed(false);
        info.setCancelled(false);
        info.setDelayDuration(0);
        info.setDeparturePlatformNumber("5a");
        info.setArrivalPlatformNumber("8b");
        info.setTimeUntilDeparture("25 minutes");

        System.out.println("=== Test Endpoint Debug ===");
        System.out.println("TrainInfo object before returning: " + info); // Use the toString() method

        return ResponseEntity.ok(info);
    }

}
