package com.seob.systeminfra.ticket.repository;

import com.seob.systeminfra.ticket.entity.TicketEntity;
import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.repository.TicketRepository;
import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TicketRepositoryImpl implements TicketRepository {

    private final TicketJpaRepository ticketJpaRepository;

    @Override
    public TicketDomain save(TicketDomain ticketDomain) {
        TicketEntity ticketEntity = toEntity(ticketDomain);
        TicketEntity saved = ticketJpaRepository.save(ticketEntity);
        return toDomain(saved);
    }

    @Override
    public Optional<TicketDomain> findById(Long id) {
        return ticketJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<TicketDomain> findByUserId(UserId userId) {
        Optional<TicketEntity> ticketEntities = ticketJpaRepository.findByUserId(userId.getValue());
        return ticketEntities.map(entity -> toDomain(entity));
    }

    @Override
    public boolean existsByUserId(UserId userId) {
        return ticketJpaRepository.existsByUserId(userId.getValue());
    }





    private TicketEntity toEntity(TicketDomain domain) {
        return new TicketEntity(
                domain.getId().getValue(),
                domain.getUserId().getValue(),
                domain.getCreatedAt(),
                domain.isUsed()
        );
    }

    // 엔티티 -> 도메인 변환
    private TicketDomain toDomain(TicketEntity entity) {
        return TicketDomain.of(
                entity.getId(),
                new UserId(entity.getUserId()), // UserId 생성자에 저장된 String 전달
                entity.getCreatedAt(),
                entity.isUsed()
        );
    }

}
