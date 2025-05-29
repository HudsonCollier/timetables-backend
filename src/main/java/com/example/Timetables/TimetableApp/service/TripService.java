//package com.example.Timetables.TimetableApp.service;
//
//import com.example.Timetables.TimetableApp.model.User;
//// Removed unused import com.example.Timetables.TimetableApp.model.TrainInfo;
//import com.example.Timetables.TimetableApp.repository.TripRepository;
//import com.example.Timetables.TimetableApp.repository.UserRepository;
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient; // Keep this import
//
//import java.util.List;
//
//@Service
//public class TripService {
//    private final TripRepository tripRepository;
//    private final UserRepository userRepository;
//    private final WebClient v2Client;
//
//    public TripService(
//            TripRepository tripRepository,
//            UserRepository userRepository,
//
//            WebClient.Builder webClientBuilder,
//            @Value("${ns.api.key}") String apiKey
//    ) {
//        this.tripRepository = tripRepository;
//        this.userRepository = userRepository;
//        this.v2Client = webClientBuilder
//                .baseUrl("https://gateway.apiportal.ns.nl/reisinformatie-api/api/v2")
//                .defaultHeader("Ocp-Apim-Subscription-Key", apiKey)
//                .build();
//    }
//
////    public Trip searchAndSaveTrip(String departingStation, String arrivalStation, long trainNumber, String email)
////    {
////        return null;
////    }
////
////    public List<Trip> getUserTrips(String username) {
////        User user = userRepository.findByUsername(username)
////                .orElseThrow(() -> new RuntimeException("User not found"));
////        return tripRepository.findByUserId(user.getId());
////    }
////
////    @Transactional
////    public void deleteTrip(Long tripId, String username) {
////        User user = userRepository.findByEmail(username)
////                .orElseThrow(() -> new RuntimeException("User not found"));
////
////        Trip trip = tripRepository.findById(tripId)
////                .orElseThrow(() -> new RuntimeException("Trip not found"));
////
////        if (!trip.getUser().getId().equals(user.getId())) {
////            throw new RuntimeException("User not authorized to delete this trip");
////        }
////
////        tripRepository.delete(trip);
////    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
