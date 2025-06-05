package com.example.Timetables.TimetableApp.controller;

import com.example.Timetables.TimetableApp.model.Passport;
import com.example.Timetables.TimetableApp.service.PassportService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/passport")
public class PassportController {
    private final PassportService passportService;

    public PassportController(PassportService passportService) {
        this.passportService = passportService;
    }

    @GetMapping("/all")
    public ResponseEntity<Passport> getUserPassport(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Passport passport = passportService.getUsersPassport(username);
        System.out.println("Made it to end of get users passport");
        return ResponseEntity.ok(passport);
    }
}
