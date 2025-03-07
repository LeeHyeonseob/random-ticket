package com.seob.systemdomain.entry.service;

import com.seob.systemdomain.entry.domain.EntryDomain;
import com.seob.systemdomain.entry.dto.ParticipantInfo;
import com.seob.systemdomain.entry.dto.UserEventInfo;


import java.util.List;

public interface EntryService {

    EntryDomain apply(String userId, Long eventId, String ticketId);

    List<EntryDomain> findByEventId(Long eventId);

    List<EntryDomain> findByUserId(String userId);

    List<ParticipantInfo> findParticipantDetailsByEventId(Long eventId);

    List<UserEventInfo> findUserEventInfoByUserId(String userId);

}
