package com.seob.systeminfra.ticket.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seob.systemdomain.ticket.domain.vo.TicketId;
import com.seob.systeminfra.ticket.entity.QTicketEntity;
import com.seob.systeminfra.ticket.entity.TicketEntity;
import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.repository.TicketRepository;
import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TicketRepositoryImpl implements TicketRepository {

    private final TicketJpaRepository ticketJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public TicketDomain save(TicketDomain ticketDomain) {
        TicketEntity ticketEntity = toEntity(ticketDomain);
        TicketEntity saved = ticketJpaRepository.save(ticketEntity);
        return toDomain(saved);
    }

    @Override
    public Optional<TicketDomain> findById(TicketId id) {
        return ticketJpaRepository.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public Optional<TicketDomain> findByUserId(UserId userId) {
        QTicketEntity ticket = QTicketEntity.ticketEntity;
        
        TicketEntity result = queryFactory
                .selectFrom(ticket)
                .where(ticket.userId.eq(userId.getValue()))
                .fetchFirst();
        
        return Optional.ofNullable(result).map(this::toDomain);
    }

    @Override
    public Optional<TicketDomain> findByUserIdAndEventIdAndNotUsed(UserId userId, Long eventId) {
        QTicketEntity ticket = QTicketEntity.ticketEntity;
        
        TicketEntity result = queryFactory
                .selectFrom(ticket)
                .where(ticket.userId.eq(userId.getValue())
                        .and(ticket.eventId.eq(eventId))
                        .and(ticket.isUsed.eq(false))
                        .and(ticket.isExpired.eq(false)))
                .fetchFirst();
        
        return Optional.ofNullable(result).map(this::toDomain);
    }

    @Override
    public Optional<TicketDomain> findByUserIdAndNotUsed(UserId userId) {
        QTicketEntity ticket = QTicketEntity.ticketEntity;
        
        TicketEntity result = queryFactory
                .selectFrom(ticket)
                .where(ticket.userId.eq(userId.getValue())
                        .and(ticket.isUsed.eq(false))
                        .and(ticket.isExpired.eq(false)))
                .fetchFirst();
        
        return Optional.ofNullable(result).map(this::toDomain);
    }

    @Override
    public boolean existsByUserId(UserId userId) {
        QTicketEntity ticket = QTicketEntity.ticketEntity;
        
        return queryFactory
                .selectOne()
                .from(ticket)
                .where(ticket.userId.eq(userId.getValue()))
                .fetchFirst() != null;
    }
    
    @Override
    public Page<TicketDomain> findByUserIdWithFilters(String userId, Boolean used, Boolean expired, Pageable pageable) {
        QTicketEntity ticket = QTicketEntity.ticketEntity;
        
        // 조건 생성
        BooleanExpression predicate = ticket.userId.eq(userId);
        if (used != null) {
            predicate = predicate.and(ticket.isUsed.eq(used));
        }
        if (expired != null) {
            predicate = predicate.and(ticket.isExpired.eq(expired));
        }
        
        // 총 개수 조회
        Long total = Optional.ofNullable(
                queryFactory
                        .select(ticket.count())
                        .from(ticket)
                        .where(predicate)
                        .fetchOne()
        ).orElse(0L);
        
        // 페이지 조회
        List<TicketEntity> tickets = queryFactory
                .selectFrom(ticket)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(ticket.createdAt.desc())
                .fetch();
        
        // 도메인 객체로 변환
        List<TicketDomain> ticketDomains = tickets.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        
        return new PageImpl<>(ticketDomains, pageable, total);
    }
    
    @Override
    public List<TicketDomain> findByIsUsedFalseAndIsExpiredFalseAndExpiryDateLessThan(LocalDateTime now) {
        QTicketEntity ticket = QTicketEntity.ticketEntity;
        
        List<TicketEntity> tickets = queryFactory
                .selectFrom(ticket)
                .where(ticket.isUsed.eq(false)
                        .and(ticket.isExpired.eq(false))
                        .and(ticket.expiryDate.lt(now)))
                .fetch();
        
        return tickets.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private TicketEntity toEntity(TicketDomain domain) {
        return new TicketEntity(
                domain.getId().getValue(),
                domain.getUserId().getValue(),
                domain.getEventId(),
                domain.getCreatedAt(),
                domain.isUsed(),
                domain.getUsedAt(),
                domain.getExpiryDate(),
                domain.getIsExpired()
        );
    }

    // 엔티티 -> 도메인 변환
    private TicketDomain toDomain(TicketEntity entity) {
        return TicketDomain.of(
                entity.getId(),
                new UserId(entity.getUserId()),
                entity.getEventId(),
                entity.getCreatedAt(),
                entity.getUsedAt(),
                entity.getExpiryDate(),
                entity.isUsed(),
                entity.isExpired()
        );
    }
}
