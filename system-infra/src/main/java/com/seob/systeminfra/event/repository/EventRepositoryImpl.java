package com.seob.systeminfra.event.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seob.systemdomain.event.dto.EventDisplayInfo;
import com.seob.systeminfra.event.entity.EventEntity;
import com.seob.systeminfra.event.entity.QEventEntity;
import com.seob.systeminfra.event.exception.EventNotFoundException;
import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.event.vo.EventStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {

    private final EventJpaRepository eventJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public EventDomain save(EventDomain eventDomain) {
        EventEntity eventEntity = toEntity(eventDomain);
        EventEntity saved = eventJpaRepository.save(eventEntity);
        return toDomain(saved);
    }

    @Override
    public EventDomain findById(Long id) {
        EventEntity eventEntity = eventJpaRepository.findById(id)
                .orElseThrow(() -> EventNotFoundException.EXCEPTION);
        return toDomain(eventEntity);
    }

    @Override
    public List<EventDomain> findAll() {
        return eventJpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public EventStatus findStatusById(Long id) {
        return eventJpaRepository.findStatusById(id);
    }

    @Override
    public Long findIdByDate(LocalDate date) {
        QEventEntity event = QEventEntity.eventEntity;
        return queryFactory
                .select(event.id)
                .from(event)
                .where(event.eventDate.eq(date))
                .fetchOne();
    }

    @Override
    public EventDisplayInfo findDisplayInfoById(Long id) {
        QEventEntity event = QEventEntity.eventEntity;

        return queryFactory
                .select(Projections.constructor(EventDisplayInfo.class,
                        event.name,
                        event.description,
                        event.status.stringValue(), // Enum -> String으로 변환 name 안됨
                        event.eventDate))
                .from(event)
                .where(event.id.eq(id))
                .fetchOne();
    }

    @Override
    public List<EventDisplayInfo> findAllDisplayInfo() {
        QEventEntity event = QEventEntity.eventEntity;

        return queryFactory
                .select(Projections.constructor(EventDisplayInfo.class,
                        event.name,
                        event.description,
                        event.status.stringValue(),
                        event.eventDate))
                .from(event)
                .fetch(); //
    }



    EventEntity toEntity(EventDomain eventDomain) {

        return new EventEntity(
                eventDomain.getId(),
                eventDomain.getName(),
                eventDomain.getDescription(),
                eventDomain.getStatus(),
                eventDomain.getEventDate(),
                eventDomain.getCreatedAt()
        );
    }

    EventDomain toDomain(EventEntity eventEntity) {
        return EventDomain.of(
                eventEntity.getId(),
                eventEntity.getName(),
                eventEntity.getDescription(),
                eventEntity.getStatus(),
                eventEntity.getEventDate(),
                eventEntity.getCreatedAt()
        );
    }


}
