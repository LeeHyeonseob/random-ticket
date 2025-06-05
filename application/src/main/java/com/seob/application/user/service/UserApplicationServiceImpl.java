package com.seob.application.user.service;

import com.seob.application.user.controller.dto.UserProfileRequest;
import com.seob.application.user.controller.dto.UserProfileResponse;
import com.seob.application.user.controller.dto.UserTicketResponse;
import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.ticket.repository.TicketRepository;
import com.seob.systemdomain.user.domain.UserDomain;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.user.dto.UserProfileInfo;
import com.seob.systemdomain.user.repository.UserRepository;
import com.seob.systemdomain.user.service.UserService;
import com.seob.systeminfra.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserApplicationServiceImpl implements UserApplicationService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(UserId userId) {
        // 1. 도메인 객체 조회 및 검증
        UserDomain user = userRepository.findById(userId)
            .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        
        // 2. 도메인 객체에서 프로필 정보 추출 (또는 별도 쿼리)
        UserProfileInfo userProfile = userRepository.findProfileById(userId).get();
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
        // 1. 도메인 객체 조회 및 검증
        UserDomain user = userRepository.findById(userId)
            .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        
        // 2. 도메인 객체가 자신의 상태 변경
        user.changeNickname(request.nickname()); // 도메인 객체가 스스로 변경
        
        // 3. 저장
        UserDomain savedUser = userRepository.save(user);
        
        // 4. 응답 변환
        return UserProfileResponse.of(UserProfileInfo.of(
            savedUser.getUserId().getValue(),
            savedUser.getEmail().getValue(),
            savedUser.getNickname().getValue()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserTicketResponse> getUserTickets(UserId userId, Boolean used, Boolean expired, Pageable pageable) {
        log.info("티켓 조회 요청 - 사용자: {}, 사용됨: {}, 만료됨: {}", userId.getValue(), used, expired);
        
        // 1. 사용자 존재 검증
        userRepository.findById(userId)
            .orElseThrow(() -> UserNotFoundException.EXCEPTION); // 존재 검증만
        
        // 2. 티켓 조회
        Page<TicketDomain> ticketPage = ticketRepository.findByUserIdWithFilters(
                userId.getValue(), used, expired, pageable);
        
        // 3. 도메인 -> DTO 변환
        List<UserTicketResponse> ticketResponses = ticketPage.getContent().stream()
                .map(UserTicketResponse::of)
                .collect(Collectors.toList());
        
        log.info("티켓 조회 결과 - {}개 티켓 (총 {}개 중 {}페이지)", 
                ticketResponses.size(), ticketPage.getTotalElements(), pageable.getPageNumber() + 1);
        
        return new PageImpl<>(ticketResponses, pageable, ticketPage.getTotalElements());
    }
}
