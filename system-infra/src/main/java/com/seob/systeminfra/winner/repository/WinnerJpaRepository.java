package com.seob.systeminfra.winner.repository;

import com.seob.systeminfra.winner.entity.WinnerEntity;
import com.seob.systemdomain.winner.vo.RewardStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WinnerJpaRepository extends JpaRepository<WinnerEntity,Long> {

    Optional<WinnerEntity> findById(Long id);

    Optional<WinnerEntity> findByEventId(Long eventId);

    Optional<WinnerEntity> findByUserId(String userId);

    List<WinnerEntity> findByStatus(RewardStatus status);

    List<WinnerEntity> findBySentAtBetween(LocalDateTime start, LocalDateTime end);

    List<WinnerEntity> findByEventIdAndStatus(Long eventId, RewardStatus status);

    boolean existsByEventId(Long eventId);
}
