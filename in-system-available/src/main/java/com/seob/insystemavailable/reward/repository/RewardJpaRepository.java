package com.seob.insystemavailable.reward.repository;

import com.seob.insystemavailable.reward.entity.RewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RewardJpaRepository extends JpaRepository<RewardEntity,Long> {

    Optional<RewardEntity> findById(Long id);

    Optional<RewardEntity> findByEventId(Long eventId);
}
