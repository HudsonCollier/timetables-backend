package com.example.Timetables.TimetableApp.model.JourneyDetailsResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stop {
    @JsonProperty("id")
    private String id;

    @JsonProperty("stop")
    private StopLocation stopLocation;

    @JsonProperty("status")
    private String status;

    @JsonProperty("destination")
    private String direction;

    @JsonProperty("arrivals")
    private List<StopArrivalInfo> stopArrivalInfo;

    @JsonProperty("departures")
    private List<StopDepartureInfo> stopDepartureInfo;
}
