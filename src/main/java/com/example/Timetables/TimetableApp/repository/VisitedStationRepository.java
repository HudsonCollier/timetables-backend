package com.example.Timetables.TimetableApp.repository;

import com.example.Timetables.TimetableApp.model.Passport;
import com.example.Timetables.TimetableApp.model.VisitedStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository that determines whether a user has visited a certain station
 */
@Repository
public interface VisitedStationRepository extends JpaRepository<VisitedStation, Long> {
    boolean existsByPassportAndStationCode(Passport passport, String stationCode);
    void deleteByPassportAndStationCode(Passport passport, String stationCode);
    long countByPassport(Passport passport);
    VisitedStation findByPassportAndStationCode(Passport passport, String stationCode);
}
