package com.seob.systeminfra.ticket.repository;

import com.seob.systeminfra.ticket.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketJpaRepository extends JpaRepository<TicketEntity, String> {
    Optional<TicketEntity> findByUserId(String userId);
    boolean existsByUserId(String userId);
}
