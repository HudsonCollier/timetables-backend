package com.example.Timetables.TimetableApp.repository;

import com.example.Timetables.TimetableApp.model.Passport;
import com.example.Timetables.TimetableApp.model.VisitedStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitedStationRepository extends JpaRepository<VisitedStation, Long> {
    boolean existsByPassportAndStationCode(Passport passport, String stationCode);
}
