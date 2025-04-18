package com.example.Timetables.TimetableApp.trains.models;
import java.util.List;
import java.util.Objects;

public class TrainInfo {
    private long number;
    private String departingStation;
    private String arrivalStation;
    private String departureTime;    // ISO‑8601 timestamp
    private String arrivalTime;      // ISO‑8601 timestamp
    private boolean onTime;
    private boolean delayed;
    private boolean cancelled;
    private int platformNumber;
    private String delayDuration;

    public TrainInfo() {}

    public TrainInfo(long number,
                     String departingStation,
                     String arrivalStation,
                     String departureTime,
                     String arrivalTime,
                     boolean onTime,
                     boolean delayed,
                     boolean cancelled,
                     int platformNumber,
                     String delayDuration)
    {
        this.number = number;
        this.departingStation = departingStation;
        this.arrivalStation = arrivalStation;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.onTime = onTime;
        this.delayed = delayed;
        this.cancelled = cancelled;
        this.platformNumber = platformNumber;
        this.delayDuration = delayDuration;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getDepartingStation() {
        return departingStation;
    }

    public void setDepartingStation(String departingStation) {
        this.departingStation = departingStation;
    }

    public String getArrivalStation() {
        return arrivalStation;
    }

    public void setArrivalStation(String arrivalStation) {
        this.arrivalStation = arrivalStation;
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

    public int getPlatformNumber() {
        return platformNumber;
    }

    public void setPlatformNumber(int platformNumber) {
        this.platformNumber = platformNumber;
    }

    public String getDelayDuration() {
        return delayDuration;
    }

    public void setDelayDuration(String delayDuration) {
        this.delayDuration = delayDuration;
    }


    @Override
    public String toString() {
        return "TrainInfo{" +
                "number=" + number +
                ", departingStation='" + departingStation + '\'' +
                ", arrivalStation='" + arrivalStation + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                ", onTime=" + onTime +
                ", delayed=" + delayed +
                ", cancelled=" + cancelled +
                ", platformNumber=" + platformNumber +
                ", delayDuration='" + delayDuration + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrainInfo)) return false;
        TrainInfo that = (TrainInfo) o;
        return number == that.number &&
                onTime == that.onTime &&
                delayed == that.delayed &&
                cancelled == that.cancelled &&
                platformNumber == that.platformNumber &&
                Objects.equals(departingStation, that.departingStation) &&
                Objects.equals(arrivalStation, that.arrivalStation) &&
                Objects.equals(departureTime, that.departureTime) &&
                Objects.equals(arrivalTime, that.arrivalTime) &&
                Objects.equals(delayDuration, that.delayDuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number,
                departingStation,
                arrivalStation,
                departureTime,
                arrivalTime,
                onTime,
                delayed,
                cancelled,
                platformNumber,
                delayDuration);
    }
}
