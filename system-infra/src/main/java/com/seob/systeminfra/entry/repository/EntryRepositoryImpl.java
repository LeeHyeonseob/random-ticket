package com.seob.systeminfra.entry.repository;

import com.seob.systeminfra.entry.entity.EntryEntity;
import com.seob.systemdomain.entry.domain.EntryDomain;
import com.seob.systemdomain.entry.repository.EntryRepository;
import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EntryRepositoryImpl implements EntryRepository {

    private final EntryJpaRepository entryJpaRepository;

    @Override
    public EntryDomain save(EntryDomain entryDomain) {
        EntryEntity entryEntity = toEntity(entryDomain);
        EntryEntity saved = entryJpaRepository.save(entryEntity);
        return toDomain(saved);
    }

    @Override
    public List<EntryDomain> finaByUserId(UserId userId) {
        return entryJpaRepository.findByUserId(userId.getValue())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<EntryDomain> findByEventId(Long eventId) {
        return entryJpaRepository.findByEventId(eventId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }


    EntryEntity toEntity(EntryDomain entryDomain) {
        return new EntryEntity(
                entryDomain.getUserId().getValue(),
                entryDomain.getEventId(),
                entryDomain.getTicketId(),
                entryDomain.getCreatedAt()
        );
    }

    EntryDomain toDomain(EntryEntity entryEntity) {
        return EntryDomain.of(
                entryEntity.getId(),
                entryEntity.getUserId(),
                entryEntity.getEventId(),
                entryEntity.getTicketId(),
                entryEntity.getCreatedAt()
        );
    }
}
