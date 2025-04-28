package com.seob.systemdomain.user.dto;

import java.time.LocalDateTime;

//사용자 프로필 정보
public record UserProfileInfo(
    String userId,
    String email,
    String nickname
) {
    public static UserProfileInfo of(String userId, String email, String nickname) {
        return new UserProfileInfo(userId, email, nickname);
    }
}
