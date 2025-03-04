package com.seob.systeminfra.event.repository;

import com.seob.systeminfra.event.entity.EventEntity;
import com.seob.systeminfra.event.exception.EventNotFoundException;
import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.event.vo.EventStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {

    private final EventJpaRepository eventJpaRepository;

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

    EventEntity toEntity(EventDomain eventDomain) {

        return new EventEntity(
                eventDomain.getName(),
                eventDomain.getDescription(),
                eventDomain.getStatus(),
                eventDomain.getEventDate(),
                eventDomain.getCreatedAt()
        );
    }

    EventDomain toDomain(EventEntity eventEntity) {
        return EventDomain.of(
                eventEntity.getName(),
                eventEntity.getDescription(),
                eventEntity.getStatus(),
                eventEntity.getEventDate(),
                eventEntity.getCreatedAt()
        );
    }


}
