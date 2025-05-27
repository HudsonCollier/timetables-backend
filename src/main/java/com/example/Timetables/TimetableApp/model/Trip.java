package com.example.Timetables.TimetableApp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "trips")
@Getter
@Setter
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "train_number")
    private String trainNumber;

    @Column(name = "departure_station")
    private String departureStation;

    @Column(name = "arrival_station")
    private String arrivalStation;

    @Column(name = "direction")
    private String direction;

    @Column(name = "departure_time")
    private LocalDateTime departureTime;

    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;

    @Column(name = "on_time")
    private boolean onTime;

    private boolean delayed;

    private boolean cancelled;

    @Column(name = "delay_duration")
    private Integer delayDuration;

    @Column(name = "departure_platform_number")
    private String departurePlatformNumber;

    @Column(name = "arrival_platform_number")
    private String arrivalPlatformNumber;

    @Column(name = "time_until_departure")
    private Integer timeUntilDeparture;

    @Column(name = "trip_date")
    private LocalDateTime date;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime updatedAt;
}