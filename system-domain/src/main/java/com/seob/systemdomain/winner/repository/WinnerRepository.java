package com.seob.systemdomain.winner.repository;

import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.winner.domain.WinnerDomain;
import com.seob.systemdomain.winner.vo.RewardStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WinnerRepository {

    WinnerDomain save(WinnerDomain winnerDomain);

    Optional<WinnerDomain> findById(Long winnerId);

    Optional<WinnerDomain> findByEventId(Long eventId);
    
    Optional<WinnerDomain> findByEntryId(Long entryId);

    List<WinnerDomain> findByStatus(RewardStatus status);

    boolean existsByEventId(Long eventId);
    
    boolean existsByEntryId(Long entryId);
}
