package com.seob.application.user.service;

import com.seob.application.common.utils.SecurityUtils;
import com.seob.application.user.controller.dto.UserProfileRequest;
import com.seob.application.user.controller.dto.UserProfileResponse;
import com.seob.application.user.controller.dto.UserTicketResponse;
import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.domain.vo.TicketId;
import com.seob.systemdomain.ticket.dto.TicketInfo;
import com.seob.systemdomain.ticket.repository.TicketRepository;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.user.dto.UserProfileInfo;
import com.seob.systemdomain.user.repository.UserRepository;
import com.seob.systemdomain.user.service.UserService;
import com.seob.systeminfra.entry.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserApplicationServiceImpl implements UserApplicationService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final TicketRepository ticketRepository;

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
    public Page<UserTicketResponse> getUserTickets(Boolean used, Boolean expired, Pageable pageable) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        log.info("티켓 조회 요청 - 사용자: {}, 사용됨: {}, 만료됨: {}", currentUserId, used, expired);
        
        // 필터링이 적용된 단일 쿼리로 티켓 조회
        Page<TicketDomain> ticketPage = ticketRepository.findByUserIdWithFilters(
                currentUserId, used, expired, pageable);
        
        // 도메인 객체를 DTO로 변환
        List<UserTicketResponse> ticketResponses = ticketPage.getContent().stream()
                .map(UserTicketResponse::of)
                .collect(Collectors.toList());
        
        log.info("티켓 조회 결과 - {}개 티켓 (총 {}개 중 {}페이지)", 
                ticketResponses.size(), ticketPage.getTotalElements(), pageable.getPageNumber() + 1);
        
        return new PageImpl<>(ticketResponses, pageable, ticketPage.getTotalElements());
    }

    

}