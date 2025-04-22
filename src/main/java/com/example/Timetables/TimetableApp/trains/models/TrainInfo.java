package com.example.Timetables.TimetableApp.trains.models;

import java.util.List;
import java.util.Objects;

public class TrainInfo {
    private long trainNumber;
    private String departureStation;
    private String arrivalStation;
    private String direction;
    private String departureTime;
    private String arrivalTime;
    private boolean onTime;
    private boolean delayed;
    private boolean cancelled;
    private int delayDuration;  // Fixed from String to int
    private String departurePlatformNumber;
    private String arrivalPlatformNumber;
    private List<TrainStop> stops;
    private String timeUntilDeparture;

    public TrainInfo() {}

    public long getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(long trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(String departureStation) {
        this.departureStation = departureStation;
    }

    public String getArrivalStation() {
        return arrivalStation;
    }

    public void setArrivalStation(String arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public boolean isOnTime() {
        return onTime;
    }

    public void setOnTime(boolean onTime) {
        this.onTime = onTime;
    }

    public boolean isDelayed() {
        return delayed;
    }

    public void setDelayed(boolean delayed) {
        this.delayed = delayed;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public int getDelayDuration() {
        return delayDuration;
    }

    public void setDelayDuration(int delayDuration) {
        this.delayDuration = delayDuration;
    }

    public String getDeparturePlatformNumber() {
        return departurePlatformNumber;
    }

    public void setDeparturePlatformNumber(String departurePlatformNumber) {
        this.departurePlatformNumber = departurePlatformNumber;
    }

    public String getArrivalPlatformNumber() {
        return arrivalPlatformNumber;
    }

    public void setArrivalPlatformNumber(String arrivalPlatformNumber) {
        this.arrivalPlatformNumber = arrivalPlatformNumber;
    }

    public List<TrainStop> getStops() {
        return stops;
    }

    public void setStops(List<TrainStop> stops) {
        this.stops = stops;
    }

    public String getTimeUntilDeparture() {
        return timeUntilDeparture;
    }

    public void setTimeUntilDeparture(String timeUntilDeparture) {
        this.timeUntilDeparture = timeUntilDeparture;
    }


    @Override
    public String toString() {
        return "TrainInfo{" +
                "trainNumber=" + trainNumber +
                ", departureStation='" + departureStation + '\'' +
                ", arrivalStation='" + arrivalStation + '\'' +
                ", direction='" + direction + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                ", onTime=" + onTime +
                ", delayed=" + delayed +
                ", cancelled=" + cancelled +
                ", delayDuration=" + delayDuration +  // updated
                ", departurePlatformNumber='" + departurePlatformNumber + '\'' +
                ", arrivalPlatformNumber='" + arrivalPlatformNumber + '\'' +
                ", stops=" + stops +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrainInfo)) return false;
        TrainInfo that = (TrainInfo) o;
        return trainNumber == that.trainNumber &&
                onTime == that.onTime &&
                delayed == that.delayed &&
                cancelled == that.cancelled &&
                delayDuration == that.delayDuration &&  // updated
                Objects.equals(departureStation, that.departureStation) &&
                Objects.equals(arrivalStation, that.arrivalStation) &&
                Objects.equals(direction, that.direction) &&
                Objects.equals(departureTime, that.departureTime) &&
                Objects.equals(arrivalTime, that.arrivalTime) &&
                Objects.equals(departurePlatformNumber, that.departurePlatformNumber) &&
                Objects.equals(arrivalPlatformNumber, that.arrivalPlatformNumber) &&
                Objects.equals(stops, that.stops);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainNumber, departureStation, arrivalStation, direction,
                departureTime, arrivalTime, onTime, delayed, cancelled,
                delayDuration, departurePlatformNumber, arrivalPlatformNumber, stops); // updated
    }
}
