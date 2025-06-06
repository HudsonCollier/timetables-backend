package com.example.Timetables.TimetableApp.responses;

import lombok.Getter;
import lombok.Setter;

/**
 * Response for when a user logs in to the application
 */
@Getter
@Setter
public class LoginResponse {
    private String token;
    private long expiresIn;

    public LoginResponse(String token, long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }
}