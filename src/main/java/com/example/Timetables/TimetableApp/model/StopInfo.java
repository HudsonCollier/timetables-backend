package com.example.Timetables.TimetableApp.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StopInfo {
    private String stationName;
    private String stationCode;
    private String arrivalTime;
    private String departureTime;
    private boolean cancelled;
    private String arrivalPlatform;
    private String departurePlatform;
    private int delayInSeconds;
    private String status;
    private double latitude;
    private double longitude;
}
