package com.seob.systeminfra.user.repository;

import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.user.dto.UserProfileInfo;
import com.seob.systeminfra.user.entity.UserEntity;
import com.seob.systemdomain.user.domain.UserDomain;
import com.seob.systemdomain.user.domain.vo.Email;
import com.seob.systemdomain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;


    @Override
    public Optional<UserDomain> findById(UserId userId) {
        Optional<UserEntity> userEntity = userJpaRepository.findById(userId.getValue());
        return userEntity.map(this::toDomain);
    }

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

    @Override
    public Optional<UserProfileInfo> findProfileById(UserId userId) {
        Optional<UserEntity> userEntity = userJpaRepository.findById(userId.getValue());
        
        if (userEntity.isPresent()) {
            UserEntity entity = userEntity.get();
            UserProfileInfo profileInfo = UserProfileInfo.of(
                entity.getUserId(),
                entity.getEmail(),
                entity.getNickname(),
                null, // createdAt 정보 없음
                null  // updatedAt 정보 없음
            );
            return Optional.of(profileInfo);
        }
        
        return Optional.empty();
    }

    @Override
    public List<UserId> findByNameAndEmail(String name, String email) {
        // 닉네임과 이메일로 사용자 엔티티 목록 조회
        List<UserEntity> entities = userJpaRepository.findByNicknameAndEmail(name, email);
        
        // 각 엔티티에서 UserId 추출하여 반환
        return entities.stream()
                .map(entity -> UserId.of(entity.getUserId()))
                .collect(Collectors.toList());
    }

    private UserEntity toEntity(UserDomain userDomain) {
        return new UserEntity(
                userDomain.getUserId().getValue(),
                userDomain.getEmail().getValue(),
                userDomain.getNickname().getValue(),
                userDomain.getPassword().getEncodedValue(),
                userDomain.getRole(),
                userDomain.isActive()
        );
    }

    private UserDomain toDomain(UserEntity userEntity) {
        return UserDomain.of(
                userEntity.getUserId(),
                userEntity.getEmail(),
                userEntity.getNickname(),
                userEntity.getPassword(),
                userEntity.getRole(),
                userEntity.isActive()
        );
    }
}
