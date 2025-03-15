package com.seob.systeminfra.entry.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seob.systemdomain.entry.dto.ParticipantInfo;
import com.seob.systemdomain.entry.dto.UserEventInfo;
import com.seob.systeminfra.entry.entity.EntryEntity;
import com.seob.systemdomain.entry.domain.EntryDomain;
import com.seob.systemdomain.entry.repository.EntryRepository;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systeminfra.entry.entity.QEntryEntity;
import com.seob.systeminfra.event.entity.QEventEntity;
import com.seob.systeminfra.user.entity.QUserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EntryRepositoryImpl implements EntryRepository {

    private final EntryJpaRepository entryJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public EntryDomain save(EntryDomain entryDomain) {
        EntryEntity entryEntity = toEntity(entryDomain);
        EntryEntity saved = entryJpaRepository.save(entryEntity);
        return toDomain(saved);
    }

    @Override
    public List<EntryDomain> findByUserId(UserId userId) {
        return entryJpaRepository.findByUserId(userId.getValue())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<EntryDomain> findByEventId(Long eventId) {
        return entryJpaRepository.findByEventId(eventId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<EntryDomain> findByUserId(String userId) {
        return entryJpaRepository.findByUserId(userId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<String> findUserIdByEventId(Long eventId) {

        return entryJpaRepository.findUserIdsByEventId(eventId);
    }

    @Override
    public List<ParticipantInfo> findParticipantDetailsByEventId(Long eventId) {
        QEntryEntity entry = QEntryEntity.entryEntity;
        QUserEntity user = QUserEntity.userEntity;


        //Tuple 한번 해보기 용도 ->  나중에 수정
        //어차피 Querydsl 내부적으로 Projections와 동일하게 작동
        List<Tuple> results = queryFactory
                .select(user.nickname, user.email, entry.createdAt)
                .from(entry)
                .join(user).on(entry.userId.eq(user.userId))
                .where(entry.eventId.eq(eventId))
                .fetch();

        // Tuple -> participantInfo dto로 변환
        return results.stream()
                .map(tuple -> ParticipantInfo.of(
                        tuple.get(user.nickname),
                        tuple.get(user.email),
                        tuple.get(entry.createdAt)
                ))
                .toList();
    }

    @Override
    public List<UserEventInfo> findUserEventInfoByUserId(String userId) {
        QEntryEntity entry = QEntryEntity.entryEntity;
        QEventEntity event = QEventEntity.eventEntity;

        return queryFactory
                .select(Projections.constructor(UserEventInfo.class,
                        event.name,
                        event.eventDate,
                        entry.createdAt))
                .from(entry)
                .innerJoin(event).on(entry.eventId.eq(event.id))
                .where(entry.userId.eq(userId))
                .orderBy(entry.createdAt.desc())  // 최신 참가 내역부터 조회
                .fetch();
    }


    EntryEntity toEntity(EntryDomain entryDomain) {
        return new EntryEntity(
                entryDomain.getUserId().getValue(),
                entryDomain.getEventId(),
                entryDomain.getTicketId(),
                entryDomain.getCreatedAt()
        );
    }

    EntryDomain toDomain(EntryEntity entryEntity) {
        return EntryDomain.of(
                entryEntity.getId(),
                entryEntity.getUserId(),
                entryEntity.getEventId(),
                entryEntity.getTicketId(),
                entryEntity.getCreatedAt()
        );
    }
}
