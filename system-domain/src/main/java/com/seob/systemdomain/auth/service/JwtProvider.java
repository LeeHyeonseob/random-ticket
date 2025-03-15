package com.seob.systemdomain.auth.service;

import com.seob.systemdomain.user.domain.vo.UserId;

public interface JwtProvider {
    String generateAccessToken(UserId userId);
    String generateRefreshToken(UserId userId);
    UserId getUserIdFromToken(String token);
    boolean validateToken(String token);
    long getAccessTokenValidity();
    long getRefreshTokenValidity();
}
