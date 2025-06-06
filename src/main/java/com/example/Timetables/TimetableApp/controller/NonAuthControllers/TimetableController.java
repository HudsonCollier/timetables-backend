package com.example.Timetables.TimetableApp.controller.NonAuthControllers;
import com.example.Timetables.TimetableApp.model.TimetableEntry;
import com.example.Timetables.TimetableApp.service.TimetableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * REST Controller used to retrieve all of the departures for a certain station
 */
@RestController
@RequestMapping("/timetable")
public class TimetableController {
    private final TimetableService timetableService;

    public TimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    // Solely for the timetable screen
    @GetMapping("/departures")
    public ResponseEntity<List<TimetableEntry>> getTimetable(@RequestParam String stationCode) {
        return ResponseEntity.ok(timetableService.getTimetable(stationCode));
    }
}



