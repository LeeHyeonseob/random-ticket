package com.seob.systemdomain.entry.repository;

import com.seob.systemdomain.entry.domain.EntryDomain;
import com.seob.systemdomain.user.domain.vo.UserId;

import java.util.List;

public interface EntryRepository {

    EntryDomain save(EntryDomain entryDomain);

    List<EntryDomain> finaByUserId(UserId userId);

    List<EntryDomain> findByEventId(Long eventId);
}
