package com.seob.application.entry.service;

import com.seob.application.entry.service.dto.UserEntryResponse;
import com.seob.systemdomain.entry.dto.UserEventInfo;
import com.seob.systemdomain.entry.repository.EntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserEntryFacadeService {

    private final EntryRepository entryRepository;

    @Transactional(readOnly = true)
    public List<UserEntryResponse> getUserEntries(String userId) {
        // 한 번의 쿼리로 사용자의 이벤트 참가 내역과 이벤트 정보를 함께 조회
        List<UserEventInfo> userEventInfos = entryRepository.findUserEventInfoByUserId(userId);

        // DTO 변환
        return userEventInfos.stream()
                .map(info ->  UserEntryResponse.of(
                        info.eventName(),
                        info.eventDate(),
                        info.registeredAt()
                ))
                .toList();
    }
}
