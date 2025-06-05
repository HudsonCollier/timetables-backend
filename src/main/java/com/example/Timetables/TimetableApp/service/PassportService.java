package com.example.Timetables.TimetableApp.service;

import com.example.Timetables.TimetableApp.model.Passport;
import com.example.Timetables.TimetableApp.model.Trip;
import com.example.Timetables.TimetableApp.model.User;
import com.example.Timetables.TimetableApp.model.VisitedStation;
import com.example.Timetables.TimetableApp.repository.PassportRepository;
import com.example.Timetables.TimetableApp.repository.TripRepository;
import com.example.Timetables.TimetableApp.repository.UserRepository;
import com.example.Timetables.TimetableApp.repository.VisitedStationRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class PassportService {
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final PassportRepository passportRepository;
    private final VisitedStationRepository visitedStationRepository;

    public PassportService(TripRepository tripRepository, UserRepository userRepository, PassportRepository passportRepository, VisitedStationRepository visitedStationRepository) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.passportRepository = passportRepository;
        this.visitedStationRepository = visitedStationRepository;
    }

    public Passport getUsersPassport(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return passportRepository.findByUser(user);
    }

    public void updatePassport(Trip trip) {
        Passport passport = passportRepository.findByUser(trip.getUser());

        if (passport == null) {
            throw new RuntimeException("Passport not found");
        }

        passport.setNumOfTrains(passport.getNumOfTrains() + 1);

        if (!visitedStationRepository.existsByPassportAndStationCode(passport, trip.getDepartureStation())) {
            VisitedStation dep = new VisitedStation();
            dep.setStationCode(trip.getDepartureStation());
            dep.setStationName(trip.getDepartureStationName());
            dep.setPassport(passport);
            visitedStationRepository.save(dep);
            passport.getVisitedStations().add(dep);
        }

        if (!visitedStationRepository.existsByPassportAndStationCode(passport, trip.getArrivalStation())) {
            VisitedStation arr = new VisitedStation();
            arr.setStationCode(trip.getArrivalStation());
            arr.setStationName(trip.getArrivalStationName());
            arr.setPassport(passport);
            visitedStationRepository.save(arr);
            passport.getVisitedStations().add(arr);
        }

        passport.setNumOfStations(passport.getVisitedStations().size());

        passport.setNumOfCountries(1);
        passport.setTotalDistance(passport.getTotalDistance() + trip.getTripDistance()); // NEED TO IMPLEMENT
        passport.setTotalDuration(passport.getTotalDuration() + trip.getTripDuration()); // NEET TO IMPLEMENT
        passport.setTotalDelayInMinutes(passport.getTotalDelayInMinutes() + trip.getDelayDuration()); // MIGHT NEED TO FIX
        passport.setAvgDelayTimeInMinutes(0); // NEED TO IMPLEMENT

        passportRepository.save(passport);
    }

}
