package com.seob.insystemavailable.ticket.repository;

import com.seob.insystemavailable.ticket.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketJpaRepository extends JpaRepository<TicketEntity, Long> {
    Optional<TicketEntity> findByUserId(String userId);
    boolean existsByUserId(String userId);
}
