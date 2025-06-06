package com.example.Timetables.TimetableApp.controller;

import com.example.Timetables.TimetableApp.model.Passport;
import com.example.Timetables.TimetableApp.service.PassportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller used for retrieving the users lifetime train data
 */
@RestController
@RequestMapping("/passport")
public class PassportController {
    private final PassportService passportService;

    public PassportController(PassportService passportService) {
        this.passportService = passportService;
    }

    @GetMapping("/")
    public ResponseEntity<Passport> getUserPassport(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Passport passport = passportService.getUsersPassport(username);
        return ResponseEntity.ok(passport);
    }
}
