package com.example.Timetables.TimetableApp.Stations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

@RestController
public class   StationController {
    private final StationService stationService;

    public StationController(StationService stationService)
    {
        this.stationService = stationService;
    }

    @GetMapping("/departures")
    public String getDepartures(@RequestParam String station)
    {
        JsonNode departures = stationService.getDepartures(station);
        System.out.println("Departures for station " + station + ":");
        System.out.println(departures.toPrettyString());
        return "Departures fetched and printed to the console!";
    }
}
