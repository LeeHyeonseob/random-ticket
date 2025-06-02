package com.seob.application.user.service;

import com.seob.application.user.controller.dto.UserProfileRequest;
import com.seob.application.user.controller.dto.UserProfileResponse;
import com.seob.application.user.controller.dto.UserTicketResponse;
import com.seob.systemdomain.ticket.domain.TicketDomain;
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
    public UserProfileResponse getUserProfile(UserId userId) {
        UserProfileInfo userProfile = userRepository.findProfileById(userId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        return UserProfileResponse.of(userProfile);
    }

    //오버로딩
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String userId) {
        return getUserProfile(UserId.of(userId));
    }

    @Override
    public UserProfileResponse updateUserProfile(UserId userId, UserProfileRequest request) {
        // 사용자 정보 업데이트
        UserProfileInfo updatedProfile = userService.updateProfile(
                userId,
                request.nickname()
        );
        
        return UserProfileResponse.of(updatedProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserTicketResponse> getUserTickets(UserId userId, Boolean used, Boolean expired, Pageable pageable) {
        log.info("티켓 조회 요청 - 사용자: {}, 사용됨: {}, 만료됨: {}", userId.getValue(), used, expired);
        
        // 필터링 적용한 단일 쿼리 티켓 조회
        Page<TicketDomain> ticketPage = ticketRepository.findByUserIdWithFilters(
                userId.getValue(), used, expired, pageable);
        
        // 도메인 -> DTO
        List<UserTicketResponse> ticketResponses = ticketPage.getContent().stream()
                .map(UserTicketResponse::of)
                .collect(Collectors.toList());
        
        log.info("티켓 조회 결과 - {}개 티켓 (총 {}개 중 {}페이지)", 
                ticketResponses.size(), ticketPage.getTotalElements(), pageable.getPageNumber() + 1);
        
        return new PageImpl<>(ticketResponses, pageable, ticketPage.getTotalElements());
    }
}