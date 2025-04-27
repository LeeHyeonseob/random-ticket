package com.seob.systemdomain.user.dto;

import java.time.LocalDateTime;

//사용자 프로필 정보
public record UserProfileInfo(
    String userId,
    String email,
    String nickname,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static UserProfileInfo of(String userId, String email, String nickname, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new UserProfileInfo(userId, email, nickname, createdAt, updatedAt);
    }
}
