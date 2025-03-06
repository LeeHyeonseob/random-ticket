package com.seob.systeminfra.entry.repository;

import com.seob.systeminfra.entry.entity.EntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EntryJpaRepository extends JpaRepository<EntryEntity, Long> {

    List<EntryEntity> findByUserId(String userId);

    List<EntryEntity> findByEventId(Long eventId);

    @Query("SELECT e.userId FROM EntryEntity e WHERE e.eventId = :eventId")
    List<String> findUserIdsByEventId(@Param("eventId") Long eventId);

}
