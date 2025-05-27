package com.seob.systeminfra.reward.repository;

import com.seob.systeminfra.reward.entity.RewardEntity;
import com.seob.systemdomain.reward.domain.RewardDomain;
import com.seob.systemdomain.reward.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Override
    public List<RewardDomain> findAll() {
        return rewardJpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        rewardJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByEventId(Long eventId) {
        return rewardJpaRepository.existsByEventId(eventId);
    }

    private RewardEntity toEntity(RewardDomain rewardDomain) {
        return new RewardEntity(
                rewardDomain.getEventId(),
                rewardDomain.getName(),
                rewardDomain.getResourceUrl(),
                rewardDomain.getCreatedAt()
        );
    }

    private RewardDomain toDomain(RewardEntity rewardEntity) {
        return RewardDomain.of(
                rewardEntity.getId(),
                rewardEntity.getEventId(),
                rewardEntity.getName(),
                rewardEntity.getResourceUrl(),
                rewardEntity.getCreatedAt()
        );
    }
}
