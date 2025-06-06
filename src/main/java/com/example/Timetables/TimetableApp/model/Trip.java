package com.example.Timetables.TimetableApp.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="trips")
public class Trip{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // FK to users id
    private User user;

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
    private String timeUntilArrival;
    private LocalDate date;

    private double tripDistance;
    private int tripDuration;

    private String departureStationName;
    private String arrivalStationName;
    private String departureCity;
    private String arrivalCity;

    private boolean isLive;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StopEntity> intermediateStops;


}
