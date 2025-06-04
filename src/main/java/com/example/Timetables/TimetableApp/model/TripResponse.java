package com.example.Timetables.TimetableApp.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TripResponse {
    private long trainNumber;
    private String departureStation;
    private String arrivalStation;
    private String direction;
    private String departureTime;
    private String arrivalTime;
    private boolean onTime;
    private boolean cancelled;
    private boolean delayed;
    private int delayDuration;
    private String departurePlatformNumber;
    private String arrivalPlatformNumber;
    private String timeUntilDeparture;
    private LocalDate date;
    private List<StopInfo> intermediateStops;

    // Adding
    private int tripDistance;
    private int tripDuration;

    //NEW
    private String departureStationName;
    private String arrivalStationName;
}
