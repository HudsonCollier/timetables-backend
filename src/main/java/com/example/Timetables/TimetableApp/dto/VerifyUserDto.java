package com.example.Timetables.TimetableApp.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO used for verifying a user
 */
@Getter
@Setter
public class VerifyUserDto {
    private String email;
    private String verificationCode;
}
