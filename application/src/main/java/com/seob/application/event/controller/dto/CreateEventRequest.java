package com.seob.application.event.controller.dto;

import java.time.LocalDate;

public record CreateEventRequest(
        String name,
        String description,
        LocalDate eventDate

) {

    public static CreateEventRequest of(String name, String description, LocalDate eventDate) {
        return new CreateEventRequest(name, description, eventDate);
    }


}
