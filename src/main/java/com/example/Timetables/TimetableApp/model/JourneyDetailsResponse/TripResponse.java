package com.example.Timetables.TimetableApp.model.JourneyDetailsResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TripResponse {
    @JsonProperty("payload")
    private JourneyPayload payload;

    public JourneyPayload getPayload() {
        return payload;
    }

    public void setPayload(JourneyPayload payload) {
        this.payload = payload;
    }
}
