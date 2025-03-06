package com.seob.systemdomain.entry.service;

import com.seob.systemdomain.entry.domain.EntryDomain;


import java.util.List;

public interface EntryService {

    EntryDomain apply(String userId, Long eventId, String ticketId);

    List<EntryDomain> findByEventId(Long eventId);

    List<EntryDomain> findByUserId(String userId);

    List<String> findUserIdByEventId(Long eventId);
}
