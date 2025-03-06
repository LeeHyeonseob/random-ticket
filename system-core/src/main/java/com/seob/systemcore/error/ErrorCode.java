package com.seob.systemcore.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통 에러
    INVALID_INPUT_VALUE(400, "C001", "Invalid input value"),
    INTERNAL_SERVER_ERROR(500, "C002", "Internal server error"),

    // 사용자 관련 에러
    USER_NOT_FOUND(404, "U001", "User not found"),
    INVALID_EMAIL_FORMAT(400, "U002", "Invalid email format"),
    INVALID_PASSWORD_FORMAT(400, "U003", "Invalid password format"),
    PASSWORD_MISMATCH(400, "U004", "Password does not match"),

    // 티켓 관련 에러
    TICKET_ALREADY_USED(400, "T001", "Ticket has already been used"),
    DUPLICATED_TICKET(400, "T002", "Duplicated ticket"),
    TICKET_ISSUANCE_EXHAUSTED(400, "T003", "All tickets have been issued"),
    TICKET_NOT_FOUND(400, "T004", "Ticket not found"),

    //이벤트 관련 에러
    EVENT_NOT_FOUND(404,"E001","Event not found"),
    EVENT_NOT_OPENED(400,"E002","Event not opened"),
    INVALID_EVENT_STATUS(400, "E003", "Invalid event status"),

    //레디스 관련 에러
    REDIS_PUBLISH_ERROR(500, "R001", "Failed to publish to Redis stream"),

    //파일 관련 에러
    FILE_UPLOAD_FAILED(500, "F001", "Failed to upload file"),
    FILE_DELETE_FAILED(500, "F002", "Failed to delete file"),
    INVALID_FILE_FORMAT(400, "F003", "Invalid file format"),
    FILE_SIZE_EXCEEDED(400, "F004", "File size exceeded"),
    FILE_NOT_FOUND(404, "F005", "File not found"),

    //보상 관련 에러
    REWARD_NOT_FOUND(404, "RW001", "Reward not found"),
    REWARD_ALREADY_EXISTS(400, "RW002", "Reward already exists for this event");
    private final int status;
    private final String code;
    private final String message;

}
