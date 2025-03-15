package com.seob.systeminfra.auth.repository;

import com.seob.systemdomain.auth.repository.EmailVerificationRepository;
import com.seob.systemdomain.user.domain.vo.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationRepositoryImpl implements EmailVerificationRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String EMAIL_VERIFICATION_PREFIX = "email_verification:";
    private static final Duration DEFAULT_EXPIRY = Duration.ofMinutes(10); // 10분
    private static final int CODE_LENGTH = 6;

    @Override
    public String generateVerificationCode() {

        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i =0; i< CODE_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    public void saveVerificationCode(Email email, String code, Duration expiry) {
        String key = EMAIL_VERIFICATION_PREFIX + email.getValue();
        redisTemplate.opsForValue().set(key,code,expiry != null ? expiry : DEFAULT_EXPIRY);
    }

    @Override
    public boolean verifyCode(Email email, String code) {
        String key = EMAIL_VERIFICATION_PREFIX + email.getValue();
        String storedCode = redisTemplate.opsForValue().get(key);
        return storedCode != null && storedCode.equals(code);
    }

    @Override
    public void deleteVerificationCode(Email email) {
        String key = EMAIL_VERIFICATION_PREFIX + email.getValue();
        redisTemplate.delete(key);
    }
}
