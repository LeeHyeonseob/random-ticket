package com.seob.application.auth.controller;

import com.seob.application.auth.CustomUserDetails;
import com.seob.application.auth.controller.dto.request.*;
import com.seob.application.auth.controller.dto.response.*;
import com.seob.application.auth.service.AuthService;
import com.seob.application.auth.service.dto.AuthServiceResponse;
import com.seob.application.auth.service.dto.ResendCodeServiceResponse;
import com.seob.application.auth.service.dto.VerificationServiceResponse;
import com.seob.systemdomain.user.domain.vo.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "인증 및 인가 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "회원가입",
        description = "새로운 사용자를 등록합니다. 이메일, 비밀번호, 닉네임 정보가 필요합니다.",
        responses = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공", 
                        content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일")
        }
    )
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request){
        UserId userId = authService.register(
                request.email(),
                request.password(),
                request.nickname()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(RegisterResponse.of(userId));
    }

    @Operation(
        summary = "이메일 인증",
        description = "가입 후 발송된 인증 코드로 이메일을 인증합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "이메일 인증 성공", 
                        content = @Content(schema = @Schema(implementation = VerificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
        }
    )
    @PatchMapping("/verification")
    public ResponseEntity<VerificationResponse> verifyEmail(@RequestBody VerifyEmailRequest request){
        VerificationServiceResponse serviceResponse = authService.verifyEmail(
                request.email(),
                request.code()
        );

        return ResponseEntity.ok(VerificationResponse.of(serviceResponse));
    }

    @Operation(
        summary = "인증 코드 재발송",
        description = "이메일 인증 코드를 재발송합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "인증 코드 재발송 성공", 
                        content = @Content(schema = @Schema(implementation = ResendCodeResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
        }
    )
    @PostMapping("/resend")
    public ResponseEntity<ResendCodeResponse> resendVerificationCode(@RequestBody ResendRequest request){
        ResendCodeServiceResponse serviceResponse = authService.resendVerificationCode(request.email());
        return ResponseEntity.ok(ResendCodeResponse.of(serviceResponse));
    }

    @Operation(
        summary = "로그인",
        description = "이메일과 비밀번호로 로그인합니다. 성공 시 액세스 토큰과 리프레시 토큰을 반환합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", 
                        content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
        }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        AuthServiceResponse serviceResponse = authService.login(request.email(), request.password());
        return ResponseEntity.ok(AuthResponse.of(serviceResponse));
    }

    @Operation(
        summary = "토큰 갱신",
        description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공", 
                        content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰")
        }
    )
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request){
        AuthServiceResponse serviceResponse = authService.refresh(request.refreshToken());
        return ResponseEntity.ok(AuthResponse.of(serviceResponse));
    }

    @Operation(
        summary = "로그아웃",
        description = "현재 로그인된 사용자를 로그아웃 처리합니다. 인증이 필요합니다.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", 
                        content = @Content(schema = @Schema(implementation = LogoutResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
        }
    )
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@AuthenticationPrincipal CustomUserDetails user){
        UserId userId = user.getUserId();
        authService.logout(userId);
        return ResponseEntity.ok(LogoutResponse.of());
    }
}
