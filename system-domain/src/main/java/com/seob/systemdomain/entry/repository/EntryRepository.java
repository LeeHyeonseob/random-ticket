package com.seob.systemdomain.entry.repository;

import com.seob.systemdomain.entry.domain.EntryDomain;
import com.seob.systemdomain.entry.dto.ParticipantInfo;
import com.seob.systemdomain.entry.dto.UserEventInfo;
import com.seob.systemdomain.user.domain.vo.UserId;

import java.util.List;

public interface EntryRepository {

    EntryDomain save(EntryDomain entryDomain);

    List<EntryDomain> findByUserId(UserId userId);

    List<EntryDomain> findByEventId(Long eventId);

    List<EntryDomain> findByUserId(String userId);

    List<String> findUserIdByEventId(Long eventId);

    List<ParticipantInfo> findParticipantDetailsByEventId(Long eventId);

    List<UserEventInfo> findUserEventInfoByUserId(String userId);
}
