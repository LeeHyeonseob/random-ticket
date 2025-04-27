package com.seob.application.winner.controller.dto;

public record WinnerCheckRequest(
    String name,
    String email
) {
    // 기본 생성자
    public WinnerCheckRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 필수 입력값입니다.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 필수 입력값입니다.");
        }
    }
}
