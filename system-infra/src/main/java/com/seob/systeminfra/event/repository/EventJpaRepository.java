package com.seob.systeminfra.event.repository;

import com.seob.systeminfra.event.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventJpaRepository extends JpaRepository<EventEntity, Long> {
    Optional<EventEntity> findById(Long id);
}
