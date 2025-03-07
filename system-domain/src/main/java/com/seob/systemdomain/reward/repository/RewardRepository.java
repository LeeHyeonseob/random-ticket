package com.seob.systemdomain.reward.repository;

import com.seob.systemdomain.reward.domain.RewardDomain;
import com.seob.systemdomain.reward.dto.RewardPreview;

import java.util.Optional;

public interface RewardRepository {

    RewardDomain save(RewardDomain rewardDomain);

    Optional<RewardDomain> findById(Long id);

    Optional<RewardDomain> findByEventId(Long eventId);







}
