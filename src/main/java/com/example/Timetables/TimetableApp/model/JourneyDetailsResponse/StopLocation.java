package com.example.Timetables.TimetableApp.model.JourneyDetailsResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StopLocation {
    @JsonProperty("name")
    private String stopName;

    @JsonProperty("uicCode")
    private int uicCode;

}
