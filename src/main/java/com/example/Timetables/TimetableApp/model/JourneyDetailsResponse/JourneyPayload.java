package com.example.Timetables.TimetableApp.model.JourneyDetailsResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * Used in order to parse JSON from the NS API Journey Details endpoint
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class JourneyPayload {
    @JsonProperty("stops")
    private List<Stop> stops;

    public List<Stop> getStops() {
        return stops;
    }
}
