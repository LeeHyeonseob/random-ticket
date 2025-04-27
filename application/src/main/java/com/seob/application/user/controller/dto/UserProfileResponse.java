package com.seob.application.user.controller.dto;

import com.seob.systemdomain.user.dto.UserProfileInfo;

import java.time.LocalDateTime;

public record UserProfileResponse(
    String userId,
    String email,
    String nickname,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static UserProfileResponse of(UserProfileInfo info) {
        return new UserProfileResponse(
            info.userId(),
            info.email(),
            info.nickname(),
            info.createdAt(),
            info.updatedAt()
        );
    }
}
