package com.seob.application.auth;

import com.seob.systemdomain.auth.service.JwtProvider;
import com.seob.systemdomain.user.domain.UserDomain;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String token = jwtAuthenticationToken.getToken();

        if (!jwtProvider.validateToken(token)) {
            throw new BadCredentialsException("Invalid JWT token");
        }

        //사용자 조회
        UserId userId = jwtProvider.getUserIdFromToken(token);
        UserDomain user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for ID: " + userId));

        //권한 설정
        CustomUserDetails userDetails = new CustomUserDetails(
                user.getUserId().getValue(),
                user.getEmail().getValue(),
                user.getRole().name()
        );

        return new JwtAuthenticationToken(userDetails, token, userDetails.getAuthorities());


    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
