package com.seob.systeminfra.reward.repository;

import com.seob.systeminfra.reward.entity.RewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RewardJpaRepository extends JpaRepository<RewardEntity, Long> {
    
    Optional<RewardEntity> findByEventId(Long eventId);
    
    boolean existsByEventId(Long eventId);
}
