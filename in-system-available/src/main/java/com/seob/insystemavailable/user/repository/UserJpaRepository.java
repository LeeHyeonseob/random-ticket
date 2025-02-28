package com.seob.insystemavailable.user.repository;

import com.seob.insystemavailable.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
