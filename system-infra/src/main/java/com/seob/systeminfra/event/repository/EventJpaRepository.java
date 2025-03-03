package com.seob.systeminfra.event.repository;

import com.seob.systeminfra.event.entity.EventEntity;
import com.seob.systemdomain.event.vo.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventJpaRepository extends JpaRepository<EventEntity, Long> {

    Optional<EventEntity> findById(Long id);

    @Query("SELECT e.status FROM EventEntity e WHERE e.id = :id")
    EventStatus findStatusById(@Param("id") Long id);
}
