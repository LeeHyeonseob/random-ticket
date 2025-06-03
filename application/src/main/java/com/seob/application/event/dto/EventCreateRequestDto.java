package com.seob.application.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateRequestDto {
    private String name;
    private String description;
    private LocalDate eventDate;
}
