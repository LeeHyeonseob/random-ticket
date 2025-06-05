package com.seob.systeminfra.user.service;

import com.seob.systemdomain.user.domain.PasswordHasher;
import com.seob.systemdomain.user.domain.UserDomain;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.user.dto.UserProfileInfo;
import com.seob.systemdomain.user.repository.UserRepository;
import com.seob.systemdomain.user.service.UserService;
import com.seob.systeminfra.exception.UserNotFoundException;
import com.seob.systeminfra.user.exception.UserDataAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    @Override
    public UserProfileInfo updateProfile(UserId userId, String nickname) {
        UserDomain user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);

        user.changeNickname(nickname);

        UserDomain savedUser = userRepository.save(user);
        
        // 프로필 정보 반환
        return UserProfileInfo.of(
            savedUser.getUserId().getValue(),
            savedUser.getEmail().getValue(),
            savedUser.getNickname().getValue()
        );
    }

    @Override
    public boolean changePassword(UserId userId, String currentPassword, String newPassword) {
        // 인프라 계층에서는 데이터 접근만 수행
        UserDomain user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        
        try {

            user.changePassword(currentPassword, newPassword, passwordHasher);
            userRepository.save(user);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
