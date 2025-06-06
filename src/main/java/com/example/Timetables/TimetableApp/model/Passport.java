package com.example.Timetables.TimetableApp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Passport entity, used to store all of the users lifetime train data, such as distance travelled, time spent on trains,
 * countries visited, etc...
 */
@Entity
@Getter
@Setter
@Table(name="passport")
public class Passport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private int numOfTrains;
    private int numOfStations;
    private int numOfCountries;
    private double totalDistance;
    private float totalDuration;
    private int totalDelayInMinutes;
    private float avgDelayTimeInMinutes;

    @OneToMany(mappedBy = "passport", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VisitedStation> visitedStations = new HashSet<>();
}
