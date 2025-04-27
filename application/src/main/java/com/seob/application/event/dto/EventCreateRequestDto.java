package com.seob.application.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 이벤트 생성 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateRequestDto {
    private String name;
    private String description;
    private LocalDate eventDate;
}
