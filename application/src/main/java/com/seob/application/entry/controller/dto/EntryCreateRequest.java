package com.seob.application.entry.controller.dto;

public record EntryCreateRequest(
    String userId,
    Long eventId,
    String ticketId

) {

}
