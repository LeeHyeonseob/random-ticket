package com.seob.application.auth.controller.dto.response;

import com.seob.systemdomain.user.domain.vo.UserId;

public record RegisterResponse(
        String userId,
        String message
) {
    public static RegisterResponse of(UserId userId) {
        return new RegisterResponse(
                userId.getValue(),
                "회원가입이 완료되었습니다. 이어서 이메일을 인증하세요"
        );
    }
}
