package com.example.Timetables.TimetableApp.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO used for login
 */
@Getter
@Setter
public class LoginUserDto {
    private String email;
    private String password;
}
