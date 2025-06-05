package com.example.Timetables.TimetableApp.repository;

import com.example.Timetables.TimetableApp.model.Passport;
import com.example.Timetables.TimetableApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassportRepository extends JpaRepository<Passport, Long> {
    Passport findByUser(User user);
}
