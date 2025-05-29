package com.example.Timetables.TimetableApp.model.JourneyDetailsResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StopArrivalInfo {
    @JsonProperty("plannedTime")
    private OffsetDateTime stopArrivalTime;

    @JsonProperty("delayInSeconds")
    private int delayInSeconds;

    @JsonProperty("plannedTrack")
    private String platformNumber;

    @JsonProperty("cancelled")
    private boolean cancelled;
}
