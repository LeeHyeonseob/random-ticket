package com.seob.systeminfra.winner.service;

import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.winner.domain.WinnerDomain;
import com.seob.systemdomain.winner.exception.WinnerNotFoundException;
import com.seob.systemdomain.winner.repository.WinnerRepository;
import com.seob.systemdomain.winner.service.WinnerService;
import com.seob.systemdomain.winner.vo.RewardStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WinnerServiceImpl implements WinnerService {

    private final WinnerRepository winnerRepository;


    @Override
    public Optional<WinnerDomain> findById(Long winnerId) {
        return winnerRepository.findById(winnerId);
    }

    @Override
    public List<WinnerDomain> findByStatus(RewardStatus status) {
        return winnerRepository.findByStatus(status);
    }

    @Override
    public boolean existsByEventId(Long eventId) {
        return winnerRepository.existsByEventId(eventId);
    }

    @Override
    public WinnerDomain createWinner(UserId userId, Long eventId, Long rewardId) {
        WinnerDomain winnerDomain = WinnerDomain.create(userId, eventId, rewardId);
        return winnerRepository.save(winnerDomain);
    }

    @Override
    public void updateStatus(Long winnerId, RewardStatus status) {
        WinnerDomain winner = winnerRepository.findById(winnerId)
                .orElseThrow(() -> WinnerNotFoundException.EXCEPTION);
        if (status == RewardStatus.COMPLETE) {
            winner.send();
        } else if (status == RewardStatus.FAILED) {
            winner.markAsFailed();
        }

        winnerRepository.save(winner);
    }
}
