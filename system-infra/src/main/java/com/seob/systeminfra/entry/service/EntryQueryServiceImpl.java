package com.seob.systeminfra.entry.service;

import com.seob.systemdomain.entry.dto.ParticipantInfo;
import com.seob.systemdomain.entry.dto.UserEventInfo;
import com.seob.systemdomain.entry.repository.EntryRepository;
import com.seob.systemdomain.entry.service.EntryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EntryQueryServiceImpl implements EntryQueryService {

    private final EntryRepository entryRepository;

    @Override
    public List<ParticipantInfo> findParticipantDetailsByEventId(Long eventId) {
        return entryRepository.findParticipantDetailsByEventId(eventId);
    }

    @Override
    public List<UserEventInfo> findUserEventInfoByUserId(String userId) {
        return entryRepository.findUserEventInfoByUserId(userId);
    }
}
