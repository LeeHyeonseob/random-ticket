package com.seob.application.user.service;

import com.seob.application.common.utils.SecurityUtils;
import com.seob.application.user.controller.dto.UserProfileRequest;
import com.seob.application.user.controller.dto.UserProfileResponse;
import com.seob.application.user.controller.dto.UserTicketResponse;
import com.seob.systemdomain.ticket.dto.TicketInfo;
import com.seob.systemdomain.ticket.repository.TicketQueryRepository;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.user.dto.UserProfileInfo;
import com.seob.systemdomain.user.repository.UserRepository;
import com.seob.systemdomain.user.service.UserService;
import com.seob.systeminfra.entry.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserApplicationServiceImpl implements UserApplicationService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final TicketQueryRepository ticketQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        String currentUserId = SecurityUtils.getCurrentUserId();
        return getUserProfile(currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String userId) {
        UserProfileInfo userProfile = userRepository.findProfileById(UserId.of(userId))
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        return UserProfileResponse.of(userProfile);
    }

    @Override
    public UserProfileResponse updateUserProfile(UserProfileRequest request) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        
        // 사용자 정보 업데이트
        UserProfileInfo updatedProfile = userService.updateProfile(
                UserId.of(currentUserId),
                request.nickname()
        );
        
        return UserProfileResponse.of(updatedProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserTicketResponse> getUserTickets() {
        String currentUserId = SecurityUtils.getCurrentUserId();
        
        // 사용자의 티켓 목록 조회
        List<TicketInfo> tickets = ticketQueryRepository.findByUserId(currentUserId);
        
        return tickets.stream()
                .map(UserTicketResponse::of)
                .toList();
    }
}
