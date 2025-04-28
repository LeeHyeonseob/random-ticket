package com.seob.systeminfra.user.service;

import com.seob.systemdomain.user.domain.PasswordHasher;
import com.seob.systemdomain.user.domain.UserDomain;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.user.dto.UserProfileInfo;
import com.seob.systemdomain.user.repository.UserRepository;
import com.seob.systemdomain.user.service.UserService;
import com.seob.systeminfra.entry.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    @Override
    public UserProfileInfo updateProfile(UserId userId, String nickname) {
        // 사용자 찾기
        UserDomain user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        
        // 닉네임 변경
        user.changeNickname(nickname);
        
        // 저장
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
        // 사용자 찾기
        UserDomain user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        
        try {
            // 비밀번호 변경 시도
            user.changePassword(currentPassword, newPassword, passwordHasher);
            
            // 저장
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
