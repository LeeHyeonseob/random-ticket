package com.seob.systeminfra.auth.service;

import com.seob.systemdomain.auth.service.JwtProvider;
import com.seob.systemdomain.user.domain.vo.UserId;

public class JwtProviderImpl implements JwtProvider {
    @Override
    public String generateAccessToken(UserId userId) {
        return "";
    }

    @Override
    public String generateRefreshToken(UserId userId) {
        return "";
    }

    @Override
    public UserId getUserIdFromToken(String token) {
        return null;
    }

    @Override
    public boolean validateToken(String token) {
        return false;
    }

    @Override
    public long getAccessTokenValidity() {
        return 0;
    }

    @Override
    public long getRefreshTokenValidity() {
        return 0;
    }
}
