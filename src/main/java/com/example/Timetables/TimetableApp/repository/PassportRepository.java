package com.example.Timetables.TimetableApp.repository;

import com.example.Timetables.TimetableApp.model.Passport;
import com.example.Timetables.TimetableApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository that allows us to find the passport for a certain user
 */
@Repository
public interface PassportRepository extends JpaRepository<Passport, Long> {
    Passport findByUser(User user);

    @Query("SELECT p FROM Passport p JOIN p.visitedStations vs WHERE p.user = :user AND vs.stationCode = :stationCode")
    Passport findByUserAndVisitedStationCode(@Param("user") User user, @Param("stationCode") String stationCode);
}
