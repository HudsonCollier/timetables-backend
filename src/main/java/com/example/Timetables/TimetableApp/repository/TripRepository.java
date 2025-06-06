package com.example.Timetables.TimetableApp.repository;

import com.example.Timetables.TimetableApp.model.Trip;
import com.example.Timetables.TimetableApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository that allows us to find trips for a certain user
 */
public interface  TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByUserId(long id);
    Optional<Trip> findById(long id);

    @Query("SELECT COUNT(t) > 0 FROM Trip t WHERE t.user = :user AND t.id != :currentTripId AND (t.departureStation = :stationCode OR t.arrivalStation = :stationCode)")
    boolean existsByUserAndDepartureStationOrArrivalStation(
            @Param("user") User user,
            @Param("currentTripId") Long currentTripId,
            @Param("stationCode") String stationCode
    );
}
