package com.seob.systeminfra.auth.service;

import com.seob.systemdomain.auth.service.JwtProvider;
import com.seob.systemdomain.user.domain.vo.UserId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

@Service
public class JwtProviderImpl implements JwtProvider {

    private final Key key;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;
    private final com.seob.systemdomain.user.repository.UserRepository userRepository;

    public JwtProviderImpl(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity}") long accessTokenValidity,
            @Value("${jwt.refresh-token-validity}") long refreshTokenValidity,
            com.seob.systemdomain.user.repository.UserRepository userRepository) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
        this.userRepository = userRepository;
    }


    @Override
    public String generateAccessToken(UserId userId) {
        return generateToken(userId, accessTokenValidity, "ACCESS");
    }

    @Override
    public String generateRefreshToken(UserId userId) {
        return generateToken(userId, refreshTokenValidity, "REFRESH");
    }

    private String generateToken(UserId userId, long validity, String tokenType){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validity); //유효기간만큼 +
        
        // 사용자 정보로부터 역할 정보 조회
        String role = userRepository.findById(userId)
                .map(user -> user.getRole().name())
                .orElse("USER"); // 기본값 설정
        
        return Jwts.builder()
                .setSubject(userId.getValue())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("type", tokenType)
                .claim("role", role) // 역할 정보 추가
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public UserId getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return UserId.of(claims.getSubject());
    }
    
    @Override
    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("role", String.class);
    }

    @Override
    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }

    @Override
    public long getAccessTokenValidity() {
        return accessTokenValidity;
    }

    @Override
    public long getRefreshTokenValidity() {
        return refreshTokenValidity;
    }
}
