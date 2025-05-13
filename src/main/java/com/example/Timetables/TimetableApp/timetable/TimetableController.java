package com.example.Timetables.TimetableApp.timetable;
import com.example.Timetables.TimetableApp.stations.Station;
import com.example.Timetables.TimetableApp.stations.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/timetable")
public class TimetableController {
    private final TimetableService timetableService;

    public TimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    // Solely for the timetable screen
    @GetMapping("/departuresTesting")
    public ResponseEntity<List<String>> getTimetableForStation(@RequestParam String stationCode) {
        return ResponseEntity.ok(timetableService.getTimetableForStation(stationCode));
    }

    // Solely for the timetable screen
    @GetMapping("/departures")
    public ResponseEntity<List<TimetableEntry>> getTimetable(@RequestParam String stationCode) {
        return ResponseEntity.ok(timetableService.getTimetable(stationCode));
    }
}



