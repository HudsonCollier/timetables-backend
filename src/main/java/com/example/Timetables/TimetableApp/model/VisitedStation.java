package com.example.Timetables.TimetableApp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity used to store every station that a user has visited. This then helps calculate the total number of stations visited
 * on the train passport
 */
@Entity
@Table(name = "visited_stations")
@Getter
@Setter
public class VisitedStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stationCode;

    private String stationName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passport_id")
    private Passport passport;
}
