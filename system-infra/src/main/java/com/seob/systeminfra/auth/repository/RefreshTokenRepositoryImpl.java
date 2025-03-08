package com.seob.systeminfra.auth.repository;

import com.seob.systemdomain.auth.repository.RefreshTokenRepository;
import com.seob.systemdomain.user.domain.vo.UserId;

public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    @Override
    public void saveRefreshToken(UserId userId, String refreshToken, long validityInMillis) {

    }

    @Override
    public String getRefreshToken(UserId userId) {
        return "";
    }

    @Override
    public void deleteRefreshToken(UserId userId) {

    }

    @Override
    public boolean validateRefreshToken(UserId userId, String refreshToken) {
        return false;
    }
}
