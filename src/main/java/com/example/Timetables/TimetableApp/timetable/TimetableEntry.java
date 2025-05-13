package com.example.Timetables.TimetableApp.timetable;

import java.util.List;

public class TimetableEntry {
    private String departureTime;
    private String direction;
    private String trainNumber;
    private String departurePlatform;
    private List<String> intermediateStations;

    public TimetableEntry() {}

    public TimetableEntry(String departureTime, String direction, String trainNumber,
                          String departurePlatform, List<String> intermediateStations) {
        this.departureTime = departureTime;
        this.direction = direction;
        this.trainNumber = trainNumber;
        this.departurePlatform = departurePlatform;
        this.intermediateStations = intermediateStations;
    }

    // Getters and Setters
    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getDeparturePlatform() {
        return departurePlatform;
    }

    public void setDeparturePlatform(String departurePlatform) {
        this.departurePlatform = departurePlatform;
    }

    public List<String> getIntermediateStations() {
        return intermediateStations;
    }

    public void setIntermediateStations(List<String> intermediateStations) {
        this.intermediateStations = intermediateStations;
    }
}
