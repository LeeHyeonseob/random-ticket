package com.seob.insystemavailable.event.repository;

import com.seob.insystemavailable.event.entity.EventEntity;
import com.seob.insystemavailable.event.exception.EventNotFoundException;
import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.event.vo.EventStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
    public EventStatus findStatusById(Long id) {
        return eventJpaRepository.findStatusById(id);
    }

    EventEntity toEntity(EventDomain eventDomain) {

        return new EventEntity(
                eventDomain.getName(),
                eventDomain.getDescription(),
                eventDomain.getStatus(),
                eventDomain.getCreatedAt()
        );
    }

    EventDomain toDomain(EventEntity eventEntity) {
        return EventDomain.of(
                eventEntity.getName(),
                eventEntity.getDescription(),
                eventEntity.getStatus(),
                eventEntity.getCreatedAt()
        );
    }


}
