package com.example.Timetables.TimetableApp.repository;

import com.example.Timetables.TimetableApp.model.Trip;
import com.example.Timetables.TimetableApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface  TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByUserId(long id);
    Optional<Trip> findById(long id);
}
