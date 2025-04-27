package com.seob.application.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 이벤트 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDto {
    private Long id;
    private String name;
    private String description;
    private String status;
    private LocalDate eventDate;
    private LocalDateTime createdAt;
}
