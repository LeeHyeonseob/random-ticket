package com.seob.systeminfra.ticket.repository;

import com.seob.systemdomain.ticket.domain.vo.TicketId;
import com.seob.systeminfra.ticket.entity.TicketEntity;
import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.repository.TicketRepository;
import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TicketRepositoryImpl implements TicketRepository {

    private final TicketJpaRepository ticketJpaRepository;

    @Override
    public TicketDomain save(TicketDomain ticketDomain) {
        TicketEntity ticketEntity = toEntity(ticketDomain);
        TicketEntity saved = ticketJpaRepository.save(ticketEntity);
        return toDomain(saved);
    }

    @Override
    public Optional<TicketDomain> findById(TicketId id) {
        log.info("findById {}", id.getValue());
        return ticketJpaRepository.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public Optional<TicketDomain> findByUserId(UserId userId) {
        Optional<TicketEntity> ticketEntities = ticketJpaRepository.findByUserId(userId.getValue());
        return ticketEntities.map(this::toDomain);
    }

    @Override
    public Optional<TicketDomain> findByUserIdAndEventIdAndNotUsed(UserId userId, Long eventId) {
        return ticketJpaRepository.findByUserIdAndEventIdAndIsUsedFalseAndIsExpiredFalse(userId.getValue(), eventId)
                .map(this::toDomain);
    }

    @Override
    public Optional<TicketDomain> findByUserIdAndNotUsed(UserId userId) {
        return ticketJpaRepository.findByUserIdAndIsUsedFalseAndIsExpiredFalse(userId.getValue())
                .map(this::toDomain);
    }

    @Override
    public boolean existsByUserId(UserId userId) {
        return ticketJpaRepository.existsByUserId(userId.getValue());
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
