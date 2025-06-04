package com.example.Timetables.TimetableApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDto {
    private String email;
    private String password;
    private String username;

    //NEW
    private String firstName;
    private String lastName;
}
