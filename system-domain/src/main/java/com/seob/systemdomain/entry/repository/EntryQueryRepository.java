package com.seob.systemdomain.entry.repository;

import com.seob.systemdomain.entry.dto.EntryInfo;

import java.util.List;

public interface EntryQueryRepository {

    List<EntryInfo> findByUserId(String userId);

    List<EntryInfo> findByEventId(Long eventId);

    EntryInfo findById(Long entryId);
}
