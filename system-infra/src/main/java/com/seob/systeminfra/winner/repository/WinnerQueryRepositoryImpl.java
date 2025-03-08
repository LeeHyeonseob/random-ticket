package com.seob.systeminfra.winner.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seob.systemdomain.winner.dto.WinnerDetailInfo;
import com.seob.systemdomain.winner.dto.WinnerUserDetailInfo;
import com.seob.systemdomain.winner.repository.WinnerQueryRepository;
import com.seob.systemdomain.winner.vo.RewardStatus;
import com.seob.systeminfra.event.entity.QEventEntity;
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
    public List<WinnerDetailInfo> findDetailsByEventId(Long eventId) {
        QWinnerEntity winner = QWinnerEntity.winnerEntity;
        QUserEntity user = QUserEntity.userEntity;
        QEventEntity event = QEventEntity.eventEntity;

        return queryFactory
                .select(Projections.constructor(WinnerDetailInfo.class,
                        winner.id,
                        winner.userId,
                        user.nickname,
                        user.email,
                        winner.eventId,
                        event.name,
                        winner.rewardId,
                        winner.status,
                        winner.sentAt
                ))
                .from(winner)
                .innerJoin(user).on(winner.userId.eq(user.userId))
                .innerJoin(event).on(winner.eventId.eq(event.id))
                .where(winner.eventId.eq(eventId))
                .fetch();
    }

    @Override
    public List<WinnerDetailInfo> findDetailsByStatus(RewardStatus status) {
        QWinnerEntity winner = QWinnerEntity.winnerEntity;
        QUserEntity user = QUserEntity.userEntity;
        QEventEntity event = QEventEntity.eventEntity;

        return queryFactory
                .select(Projections.constructor(WinnerDetailInfo.class,
                        winner.id,
                        winner.userId,
                        user.nickname,
                        user.email,
                        winner.eventId,
                        event.name,
                        winner.rewardId,
                        winner.status,
                        winner.sentAt
                ))
                .from(winner)
                .innerJoin(user).on(winner.userId.eq(user.userId))
                .innerJoin(event).on(winner.eventId.eq(event.id))
                .where(winner.status.eq(status))
                .fetch();
    }

    @Override
    public List<WinnerDetailInfo> findAllDetails() {
        QWinnerEntity winner = QWinnerEntity.winnerEntity;
        QUserEntity user = QUserEntity.userEntity;
        QEventEntity event = QEventEntity.eventEntity;

        return queryFactory
                .select(Projections.constructor(WinnerDetailInfo.class,
                        winner.id,
                        winner.userId,
                        user.nickname,
                        user.email,
                        winner.eventId,
                        event.name,
                        winner.rewardId,
                        winner.status,
                        winner.sentAt
                ))
                .from(winner)
                .innerJoin(user).on(winner.userId.eq(user.userId))
                .innerJoin(event).on(winner.eventId.eq(event.id))
                .fetch();
    }

    @Override
    public List<WinnerUserDetailInfo> findUserDetailsByUserId(String userId) {
        QWinnerEntity winner = QWinnerEntity.winnerEntity;
        QUserEntity user = QUserEntity.userEntity;
        QEventEntity event = QEventEntity.eventEntity;

        return queryFactory
                .select(Projections.constructor(WinnerUserDetailInfo.class,
                        winner.id,
                        winner.eventId,
                        event.name,
                        event.description,
                        winner.status,
                        winner.sentAt
                ))
                .from(winner)
                .innerJoin(event).on(winner.eventId.eq(event.id))
                .where(winner.userId.eq(userId))
                .fetch();
    }
}
