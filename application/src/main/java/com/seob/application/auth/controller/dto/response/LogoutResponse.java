package com.seob.application.auth.controller.dto.response;

public record LogoutResponse(
        String message
) {
    public static LogoutResponse of() {
        return new LogoutResponse("로그아웃 성공");
    }
}
