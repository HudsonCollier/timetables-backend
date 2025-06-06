package com.example.Timetables.TimetableApp.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO used for registering a user
 */
@Getter
@Setter
public class RegisterUserDto {
    private String email;
    private String password;
    private String username;

    private String firstName;
    private String lastName;
}
