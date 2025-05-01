package com.seob.systeminfra.winner.repository;

import com.seob.systeminfra.winner.entity.WinnerEntity;
import com.seob.systemdomain.winner.domain.WinnerDomain;
import com.seob.systemdomain.winner.repository.WinnerRepository;
import com.seob.systemdomain.winner.vo.RewardStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
    public Optional<WinnerDomain> findByEntryId(Long entryId) {
        return winnerJpaRepository.findByEntryId(entryId).map(this::toDomain);
    }

    @Override
    public List<WinnerDomain> findByStatus(RewardStatus status) {
        return winnerJpaRepository.findByStatus(status)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEventId(Long eventId) {
        return winnerJpaRepository.existsByEventId(eventId);
    }
    
    @Override
    public boolean existsByEntryId(Long entryId) {
        return winnerJpaRepository.existsByEntryId(entryId);
    }

    WinnerEntity toEntity(WinnerDomain winnerDomain) {
        return new WinnerEntity(
                winnerDomain.getUserId().getValue(),
                winnerDomain.getEventId(),
                winnerDomain.getRewardId(),
                winnerDomain.getEntryId(),
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
                winnerEntity.getEntryId(),
                winnerEntity.getStatus(),
                winnerEntity.getSentAt()
        );
    }
}
