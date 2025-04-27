package com.seob.application.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 이벤트 상태 업데이트 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventStatusUpdateRequestDto {
    private String status;
}
