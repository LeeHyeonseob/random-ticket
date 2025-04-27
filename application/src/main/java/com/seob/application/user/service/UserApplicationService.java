package com.seob.application.user.service;

import com.seob.application.user.controller.dto.UserProfileRequest;
import com.seob.application.user.controller.dto.UserProfileResponse;
import com.seob.application.user.controller.dto.UserTicketResponse;

import java.util.List;

public interface UserApplicationService {

    //현재 로그인한 사용자의 프로필 정보 조회
    UserProfileResponse getCurrentUserProfile();

    //특정 사용자의 프로필 정보 조회
    UserProfileResponse getUserProfile(String userId);

    //사용자 프로필 정보 업데이트
    UserProfileResponse updateUserProfile(UserProfileRequest request);

    //현재 로그인한 사용자의 티켓 목록 조회
    List<UserTicketResponse> getUserTickets();
}
