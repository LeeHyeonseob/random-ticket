package com.seob.systeminfra.winner.repository;

import com.seob.systeminfra.winner.entity.WinnerEntity;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.winner.domain.WinnerDomain;
import com.seob.systemdomain.winner.repository.WinnerRepository;
import com.seob.systemdomain.winner.vo.RewardStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class WinnerRepositoryImpl implements WinnerRepository {

    private final WinnerJpaRepository winnerJpaRepository;

    @Override
    public WinnerDomain save(WinnerDomain winnerDomain) {
        WinnerEntity winnerEntity = toEntity(winnerDomain);
        WinnerEntity saved = winnerJpaRepository.save(winnerEntity);
        return toDomain(saved);
    }

    @Override
    public Optional<WinnerDomain> findById(Long winnerId) {
        return winnerJpaRepository.findById(winnerId).map(this::toDomain);
    }

    @Override
    public Optional<WinnerDomain> findByEventId(Long eventId) {
        return winnerJpaRepository.findByEventId(eventId).map(this::toDomain);
    }

    @Override
    public List<WinnerDomain> findByUserId(UserId userId) {
        return winnerJpaRepository.findByUserId(userId.getValue())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<WinnerDomain> findAll() {
        return winnerJpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<WinnerDomain> findByStatus(RewardStatus status) {
        return winnerJpaRepository.findByStatus(status)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<WinnerDomain> findBySentAtBetween(LocalDateTime start, LocalDateTime end) {
        return winnerJpaRepository.findBySentAtBetween(start, end)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<WinnerDomain> findByEventIdAndStatus(Long eventId, RewardStatus status) {
        return winnerJpaRepository.findByEventIdAndStatus(eventId, status)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEventId(Long eventId) {
        return winnerJpaRepository.existsByEventId(eventId);
    }

    @Override
    public void delete(WinnerDomain winnerDomain) {
        WinnerEntity entity = toEntity(winnerDomain);
        winnerJpaRepository.delete(entity);
    }


    WinnerEntity toEntity(WinnerDomain winnerDomain) {
        return new WinnerEntity(
                winnerDomain.getUserId().getValue(),
                winnerDomain.getEventId(),
                winnerDomain.getRewardId(),
                winnerDomain.getStatus(),
                winnerDomain.getSentAt()
        );
    }

    WinnerDomain toDomain(WinnerEntity winnerEntity) {
        return WinnerDomain.of(
                winnerEntity.getId(),
                winnerEntity.getUserId(),
                winnerEntity.getEventId(),
                winnerEntity.getRewardId(),
                winnerEntity.getStatus(),
                winnerEntity.getSentAt()

        );
    }
}
