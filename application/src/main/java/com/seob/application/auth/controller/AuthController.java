package com.seob.application.auth.controller;

import com.seob.application.auth.CustomUserDetails;
import com.seob.application.auth.controller.dto.request.*;
import com.seob.application.auth.controller.dto.response.*;
import com.seob.application.auth.service.AuthService;
import com.seob.application.auth.service.dto.AuthServiceResponse;
import com.seob.application.auth.service.dto.ResendCodeServiceResponse;
import com.seob.application.auth.service.dto.VerificationServiceResponse;
import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request){
        UserId userId = authService.register(
                request.email(),
                request.password(),
                request.nickname()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(RegisterResponse.of(userId));
    }

    @PostMapping("/verify")
    public ResponseEntity<VerificationResponse> verifyEmail(@RequestBody VerifyEmailRequest request){
        VerificationServiceResponse serviceResponse = authService.verifyEmail(
                request.email(),
                request.code()
        );

        return ResponseEntity.ok(VerificationResponse.of(serviceResponse));
    }

    @PostMapping("/resend")
    public ResponseEntity<ResendCodeResponse> resendVerificationCode(@RequestBody ResendRequest request){
        ResendCodeServiceResponse serviceResponse = authService.resendVerificationCode(request.email());
        return ResponseEntity.ok(ResendCodeResponse.of(serviceResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        AuthServiceResponse serviceResponse = authService.login(request.email(), request.password());
        return ResponseEntity.ok(AuthResponse.of(serviceResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request){
        AuthServiceResponse serviceResponse = authService.refresh(request.refreshToken());
        return ResponseEntity.ok(AuthResponse.of(serviceResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@AuthenticationPrincipal CustomUserDetails user){
        UserId userId = user.getUserId();
        authService.logout(userId);
        return ResponseEntity.ok(LogoutResponse.of());
    }
}
