package com.seob.systemdomain.winner.service;

import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.winner.domain.WinnerDomain;
import com.seob.systemdomain.winner.vo.RewardStatus;

import java.util.List;
import java.util.Optional;

public interface WinnerService {

    Optional<WinnerDomain> findById(Long winnerId);
    List<WinnerDomain> findByStatus(RewardStatus status);
    boolean existsByEventId(Long eventId);
    WinnerDomain createWinner(UserId userId, Long eventId, Long rewardId);
    void updateStatus(Long winnerID, RewardStatus status);






}
