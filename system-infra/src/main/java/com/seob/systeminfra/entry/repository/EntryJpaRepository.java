package com.seob.systeminfra.entry.repository;

import com.seob.systeminfra.entry.entity.EntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntryJpaRepository extends JpaRepository<EntryEntity, Long> {

    List<EntryEntity> findByUserId(String userId);

    List<EntryEntity> findByEventId(Long eventId);

}
