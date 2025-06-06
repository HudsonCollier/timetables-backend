package com.example.Timetables.TimetableApp.controller.NonAuthControllers;
import com.example.Timetables.TimetableApp.model.Station;
import com.example.Timetables.TimetableApp.service.StationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller used to handle the station search
 */
@RestController
@RequestMapping("/stations")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    // Used to populate the search bars
    @GetMapping("/search")
    public List<Station> searchStations(@RequestParam String query) {
        System.out.println("Query received: " + query);
        return stationService.getStations(query);
    }
}