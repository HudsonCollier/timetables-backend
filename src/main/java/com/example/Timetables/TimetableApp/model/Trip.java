package com.example.Timetables.TimetableApp.model;

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
    @JoinColumn(name = "user_id", nullable = false) // FK to users.id
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
    private LocalDate date;
    private int tripDistance;
    private int tripDuration;

    @Transient
    private List<StopInfo> intermediateStops;
}
