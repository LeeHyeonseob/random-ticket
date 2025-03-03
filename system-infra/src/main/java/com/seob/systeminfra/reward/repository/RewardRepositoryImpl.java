package com.seob.systeminfra.reward.repository;

import com.seob.systeminfra.reward.entity.RewardEntity;
import com.seob.systemdomain.reward.domain.RewardDomain;
import com.seob.systemdomain.reward.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RewardRepositoryImpl implements RewardRepository {

    private final RewardJpaRepository rewardJpaRepository;
    @Override
    public RewardDomain save(RewardDomain rewardDomain) {
        RewardEntity rewardEntity = toEntity(rewardDomain);
        RewardEntity saved = rewardJpaRepository.save(rewardEntity);
        return toDomain(saved);
    }

    @Override
    public Optional<RewardDomain> findById(Long id) {
        return rewardJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<RewardDomain> findByEventId(Long eventId) {
        return rewardJpaRepository.findByEventId(eventId).map(this::toDomain);
    }

    RewardEntity toEntity(RewardDomain rewardDomain) {
        return new RewardEntity(
                rewardDomain.getEventId(),
                rewardDomain.getResource_url(),
                rewardDomain.getCreatedAt()
        );
    }

    RewardDomain toDomain(RewardEntity rewardEntity) {
        return RewardDomain.of(
                rewardEntity.getId(),
                rewardEntity.getEventId(),
                rewardEntity.getResource_url(),
                rewardEntity.getCreatedAt()
        );
    }
}
