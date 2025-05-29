package com.example.Timetables.TimetableApp.model.JourneyDetailsResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class StopDepartureInfo {

    @JsonProperty("plannedTime")
    private OffsetDateTime stopDepartureTime;

    @JsonProperty("delayInSeconds")
    private int delayInSeconds;

    @JsonProperty("plannedTrack")
    private String platformNumber;

    @JsonProperty("cancelled")
    private boolean cancelled;
}
