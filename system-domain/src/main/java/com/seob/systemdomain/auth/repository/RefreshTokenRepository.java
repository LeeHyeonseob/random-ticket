package com.seob.systemdomain.auth.repository;

import com.seob.systemdomain.user.domain.vo.UserId;

public interface RefreshTokenRepository {
    void saveRefreshToken(UserId userId, String refreshToken, long validityInMillis);
    String getRefreshToken(UserId userId);
    void deleteRefreshToken(UserId userId);
    boolean validateRefreshToken(UserId userId, String refreshToken);
}
