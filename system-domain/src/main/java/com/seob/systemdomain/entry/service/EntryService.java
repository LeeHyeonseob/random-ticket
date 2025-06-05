package com.seob.systemdomain.entry.service;

import com.seob.systemdomain.entry.domain.EntryDomain;

// 이벤트 참여 관련 핵심 비즈니스 로직
public interface EntryService {

    EntryDomain apply(String userId, Long eventId);
}
