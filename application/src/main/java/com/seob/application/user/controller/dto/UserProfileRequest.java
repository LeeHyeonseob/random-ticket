package com.seob.application.user.controller.dto;

public record UserProfileRequest(
    String nickname
) {
    // 유효성 검사를 위한 기본 생성자
    public UserProfileRequest {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 필수 입력값입니다.");
        }
    }
}
