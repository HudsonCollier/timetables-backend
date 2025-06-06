package com.example.Timetables.TimetableApp.model.JourneyDetailsResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Used in order to help parse the JSON from the NS API Journey Details endpoint. Top level of the JSON.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TripResponse {
    @JsonProperty("payload")
    private JourneyPayload payload;

    public JourneyPayload getPayload() {
        return payload;
    }
}
