package com.seob.systeminfra.auth.repository;

import com.seob.systemdomain.auth.repository.RefreshTokenRepository;
import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    @Override
    public void saveRefreshToken(UserId userId, String refreshToken, long validityInMillis) {
        String key = REFRESH_TOKEN_PREFIX + userId.getValue();
        redisTemplate.opsForValue().set(key, refreshToken, validityInMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public String getRefreshToken(UserId userId) {
        String key = REFRESH_TOKEN_PREFIX + userId.getValue();
        Object token = redisTemplate.opsForValue().get(key);
        return token != null ? token.toString() : null;
    }

    @Override
    public void deleteRefreshToken(UserId userId) {
        String key = REFRESH_TOKEN_PREFIX + userId.getValue();
        redisTemplate.delete(key);
    }

    @Override
    public boolean validateRefreshToken(UserId userId, String refreshToken) {
        String token = getRefreshToken(userId);
        return token != null && token.equals(refreshToken);
    }
}
