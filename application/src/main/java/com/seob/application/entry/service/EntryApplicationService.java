package com.seob.application.entry.service;

import com.seob.application.entry.controller.dto.EntryResponse;

import java.util.List;

public interface EntryApplicationService {

    //현재 로그인한 사용자의 이벤트 참여 내역 조회
    List<EntryResponse> getMyEntries();

    //특정 사용자의 이벤트 참여 내역 조회
    List<EntryResponse> getUserEntries(String userId);

    //특정 이벤트의 참여 내역 조회
    List<EntryResponse> getEventEntries(Long eventId);
}
