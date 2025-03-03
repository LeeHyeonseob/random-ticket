package com.seob.systeminfra.user.repository;

import com.seob.systeminfra.user.entity.UserEntity;
import com.seob.systemdomain.user.domain.UserDomain;
import com.seob.systemdomain.user.domain.vo.Email;
import com.seob.systemdomain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;


    @Override
    public UserDomain save(UserDomain userDomain) {
        UserEntity userEntity = toEntity(userDomain);
        UserEntity saved = userJpaRepository.save(userEntity);
        return toDomain(saved);
    }

    @Override
    public Optional<UserDomain> findByEmail(Email email) {
        Optional<UserEntity> userEntity = userJpaRepository.findByEmail(email.getValue());
        return userEntity.map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return userJpaRepository.existsByEmail(email.getValue());
    }

    private UserEntity toEntity(UserDomain userDomain) {
        return new UserEntity(
                userDomain.getUserId().getValue(),
                userDomain.getEmail().getValue(),
                userDomain.getPassword().getEncodedValue(),
                userDomain.getRole(),
                userDomain.isActive()
        );
    }

    private UserDomain toDomain(UserEntity userEntity) {
        return UserDomain.of(
                userEntity.getUserId(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getRole(),
                userEntity.isActive()
        );
    }
}
