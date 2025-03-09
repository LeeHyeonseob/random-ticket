package com.seob.application.auth;

import com.seob.systemdomain.user.domain.vo.UserId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final String userId;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String userId, String email, String role) {
        this.userId = userId;
        this.email = email;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    public UserId getUserId() {
        return UserId.of(userId);
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // JWT 인증에서는 필요 없음
    }

    @Override
    public String getUsername() {
        return email; // Spring Security에서 식별자로 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
