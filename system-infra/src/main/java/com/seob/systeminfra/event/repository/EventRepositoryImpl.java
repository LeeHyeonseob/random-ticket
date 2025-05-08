package com.seob.systeminfra.event.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seob.systemdomain.event.dto.EventDisplayInfo;
import com.seob.systeminfra.event.entity.EventEntity;
import com.seob.systeminfra.event.entity.QEventEntity;
import com.seob.systeminfra.event.exception.EventNotFoundException;
import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.event.vo.EventStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
        QEventEntity event = QEventEntity.eventEntity;
        
        return queryFactory
                .select(event.status)
                .from(event)
                .where(event.id.eq(id))
                .fetchOne();
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
    
    @Override
    public Page<EventDomain> findAllWithFilters(String status, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        QEventEntity event = QEventEntity.eventEntity;

        // 동적 쿼리를 위한 조건 생성
        BooleanExpression statusCondition = status != null ? 
                event.status.eq(EventStatus.valueOf(status)) : null;
        BooleanExpression fromDateCondition = fromDate != null ? 
                event.eventDate.goe(fromDate) : null;
        BooleanExpression toDateCondition = toDate != null ? 
                event.eventDate.loe(toDate) : null;
        
        // 조건 조합
        BooleanExpression whereCondition = combineConditions(statusCondition, fromDateCondition, toDateCondition);
        
        // 총 개수 쿼리
        long total = queryFactory
                .selectFrom(event)
                .where(whereCondition)
                .fetchCount();
        
        // 페이징 쿼리
        List<EventEntity> events = queryFactory
                .selectFrom(event)
                .where(whereCondition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(event.eventDate.desc())
                .fetch();
        
        // 도메인 객체로 변환
        List<EventDomain> eventDomains = events.stream()
                .map(this::toDomain)
                .toList();
        
        return new PageImpl<>(eventDomains, pageable, total);
    }
    
    @Override
    public List<EventDomain> findByEventDateAndStatusNotClosed(LocalDate yesterday) {
        QEventEntity event = QEventEntity.eventEntity;
        
        List<EventEntity> entities = queryFactory
                .selectFrom(event)
                .where(event.eventDate.eq(yesterday)
                        .and(event.status.ne(EventStatus.CLOSED)))
                .fetch();
        
        // 엔티티 목록을 도메인 객체 목록으로 변환
        return entities.stream()
                .map(this::toDomain)
                .toList();
    }
    
    // 여러 조건 결합 헬퍼 메서드
    private BooleanExpression combineConditions(BooleanExpression... conditions) {
        BooleanExpression result = null;
        
        for (BooleanExpression condition : conditions) {
            if (condition != null) {
                result = result == null ? condition : result.and(condition);
            }
        }
        
        return result;
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
