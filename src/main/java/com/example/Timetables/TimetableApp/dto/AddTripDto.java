package com.example.Timetables.TimetableApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddTripDto {
    private String departureStation;
    private String arrivalStation;
    private long trainNumber;
}