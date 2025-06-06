package com.example.Timetables.TimetableApp.model.JourneyDetailsResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Used in order to help parse the JSON from the NS API Journey Details endpoint
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StopLocation {
    @JsonProperty("name")
    private String stopName;

    @JsonProperty("uicCode")
    private int uicCode;

    @JsonProperty("lat")
    private double latitude;

    @JsonProperty("lng")
    private double longitude;
}
