package com.example.Timetables.TimetableApp.trains;


import com.example.Timetables.TimetableApp.trains.models.TrainInfo;
import com.example.Timetables.TimetableApp.trains.TrainService;
import org.apache.coyote.Request;
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
}
