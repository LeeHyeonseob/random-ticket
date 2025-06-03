package com.seob.systeminfra.entry.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seob.systemdomain.entry.dto.EntryInfo;
import com.seob.systemdomain.entry.repository.EntryQueryRepository;
import com.seob.systeminfra.entry.entity.QEntryEntity;
import com.seob.systeminfra.event.entity.QEventEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class EntryQueryRepositoryImpl implements EntryQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<EntryInfo> findByUserId(String userId) {
        QEntryEntity entry = QEntryEntity.entryEntity;
        QEventEntity event = QEventEntity.eventEntity;

        return queryFactory
                .select(Projections.constructor(EntryInfo.class,
                        entry.id,
                        entry.eventId,
                        event.name,
                        entry.ticketId,
                        entry.createdAt))
                .from(entry)
                .join(event).on(entry.eventId.eq(event.id))
                .where(entry.userId.eq(userId))
                .orderBy(entry.createdAt.desc())
                .fetch();
    }

    @Override
    public List<EntryInfo> findByEventId(Long eventId) {
        QEntryEntity entry = QEntryEntity.entryEntity;
        QEventEntity event = QEventEntity.eventEntity;

        return queryFactory
                .select(Projections.constructor(EntryInfo.class,
                        entry.id,
                        entry.eventId,
                        event.name,
                        entry.ticketId,
                        entry.createdAt))
                .from(entry)
                .join(event).on(entry.eventId.eq(event.id))
                .where(entry.eventId.eq(eventId))
                .orderBy(entry.createdAt.desc())
                .fetch();
    }

    @Override
    public EntryInfo findById(Long entryId) {
        QEntryEntity entry = QEntryEntity.entryEntity;
        QEventEntity event = QEventEntity.eventEntity;

        return queryFactory
                .select(Projections.constructor(EntryInfo.class,
                        entry.id,
                        entry.eventId,
                        event.name,
                        entry.ticketId,
                        entry.createdAt))
                .from(entry)
                .join(event).on(entry.eventId.eq(event.id))
                .where(entry.id.eq(entryId))
                .fetchOne();
    }
}
