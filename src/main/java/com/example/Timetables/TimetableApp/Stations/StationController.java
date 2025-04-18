package com.example.Timetables.TimetableApp.Stations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/stations")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping("/search")
    public List<String> searchStations(@RequestParam String query) {
        System.out.println("Query received: " + query);
        return stationService.getStations(query);
    }

    @GetMapping("/timetable")
    public ResponseEntity<List<String>> getTimetable(@RequestParam String stationCode) {
        return ResponseEntity.ok(stationService.getTimetableForStation(stationCode));
    }

}