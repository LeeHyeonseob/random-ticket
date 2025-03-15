package com.seob.systemdomain.auth.repository;

import com.seob.systemdomain.user.domain.vo.Email;

import java.time.Duration;

public interface EmailVerificationRepository {
    String generateVerificationCode();
    void saveVerificationCode(Email email, String code, Duration expiry);
    boolean verifyCode(Email email, String code);
    void deleteVerificationCode(Email email);
}
