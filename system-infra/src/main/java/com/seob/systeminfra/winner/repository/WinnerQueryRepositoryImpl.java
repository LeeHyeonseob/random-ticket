package com.seob.systeminfra.winner.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seob.systemdomain.winner.dto.WinnerRewardDetailInfo;
import com.seob.systemdomain.winner.dto.WinnerUserDetailInfo;
import com.seob.systemdomain.winner.repository.WinnerQueryRepository;
import com.seob.systemdomain.winner.vo.RewardStatus;
import com.seob.systeminfra.event.entity.QEventEntity;
import com.seob.systeminfra.reward.entity.QRewardEntity;
import com.seob.systeminfra.user.entity.QUserEntity;
import com.seob.systeminfra.winner.entity.QWinnerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class WinnerQueryRepositoryImpl implements WinnerQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<WinnerRewardDetailInfo> findDetailsByEventId(Long eventId) {
        QWinnerEntity winner = QWinnerEntity.winnerEntity;
        QUserEntity user = QUserEntity.userEntity;
        QEventEntity event = QEventEntity.eventEntity;
        QRewardEntity reward = QRewardEntity.rewardEntity;

        return queryFactory
                .select(Projections.constructor(WinnerRewardDetailInfo.class,
                        winner.id,
                        winner.userId,
                        user.nickname,
                        user.email,
                        winner.eventId,
                        event.name,
                        event.description,
                        winner.rewardId,
                        reward.name,
                        winner.status,
                        winner.sentAt
                ))
                .from(winner)
                .innerJoin(event).on(winner.eventId.eq(event.id))
                .innerJoin(reward).on(winner.rewardId.eq(reward.id))
                .innerJoin(user).on(winner.userId.eq(user.userId))
                .where(winner.eventId.eq(eventId))
                .orderBy(winner.id.desc())
                .fetch();
    }

    @Override
    public List<WinnerRewardDetailInfo> findDetailsByStatus(RewardStatus status) {
        QWinnerEntity winner = QWinnerEntity.winnerEntity;
        QUserEntity user = QUserEntity.userEntity;
        QEventEntity event = QEventEntity.eventEntity;
        QRewardEntity reward = QRewardEntity.rewardEntity;

        return queryFactory
                .select(Projections.constructor(WinnerRewardDetailInfo.class,
                        winner.id,
                        winner.userId,
                        user.nickname,
                        user.email,
                        winner.eventId,
                        event.name,
                        event.description,
                        winner.rewardId,
                        reward.name,
                        winner.status,
                        winner.sentAt
                ))
                .from(winner)
                .innerJoin(event).on(winner.eventId.eq(event.id))
                .innerJoin(reward).on(winner.rewardId.eq(reward.id))
                .innerJoin(user).on(winner.userId.eq(user.userId))
                .where(winner.status.eq(status))
                .orderBy(winner.id.desc())
                .fetch();
    }

    @Override
    public List<WinnerRewardDetailInfo> findAllDetails() {
        QWinnerEntity winner = QWinnerEntity.winnerEntity;
        QUserEntity user = QUserEntity.userEntity;
        QEventEntity event = QEventEntity.eventEntity;
        QRewardEntity reward = QRewardEntity.rewardEntity;

        return queryFactory
                .select(Projections.constructor(WinnerRewardDetailInfo.class,
                        winner.id,
                        winner.userId,
                        user.nickname,
                        user.email,
                        winner.eventId,
                        event.name,
                        event.description,
                        winner.rewardId,
                        reward.name,
                        winner.status,
                        winner.sentAt
                ))
                .from(winner)
                .innerJoin(event).on(winner.eventId.eq(event.id))
                .innerJoin(reward).on(winner.rewardId.eq(reward.id))
                .innerJoin(user).on(winner.userId.eq(user.userId))
                .orderBy(winner.id.desc())
                .fetch();
    }

    @Override
    public List<WinnerUserDetailInfo> findUserDetailsByUserId(String userId) {
        QWinnerEntity winner = QWinnerEntity.winnerEntity;
        QEventEntity event = QEventEntity.eventEntity;
        QRewardEntity reward = QRewardEntity.rewardEntity;

        return queryFactory
                .select(Projections.constructor(WinnerUserDetailInfo.class,
                        winner.id,
                        winner.eventId,
                        event.name,
                        event.description,
                        reward.name,
                        winner.status,
                        winner.sentAt
                ))
                .from(winner)
                .innerJoin(event).on(winner.eventId.eq(event.id))
                .innerJoin(reward).on(winner.rewardId.eq(reward.id))
                .where(winner.userId.eq(userId))
                .orderBy(winner.id.desc())
                .fetch();
    }

    @Override
    public boolean existsByUserNameAndEmail(String userName, String email){
        QWinnerEntity winner = QWinnerEntity.winnerEntity;
        QUserEntity user = QUserEntity.userEntity;

        Integer result = queryFactory
                .selectOne()
                .from(winner)
                .innerJoin(user).on(winner.userId.eq(user.userId))
                .where(
                        user.nickname.eq(userName).and(user.email.eq(email))
                )
                .fetchFirst();

        return result != null;
    }


}
