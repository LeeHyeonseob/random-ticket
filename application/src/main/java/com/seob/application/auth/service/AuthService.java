package com.seob.application.auth.service;

import com.seob.application.auth.exception.*;
import com.seob.application.auth.service.dto.AuthServiceResponse;
import com.seob.application.auth.service.dto.ResendCodeServiceResponse;
import com.seob.application.auth.service.dto.VerificationServiceResponse;
import com.seob.systemdomain.auth.repository.EmailVerificationRepository;
import com.seob.systemdomain.auth.repository.RefreshTokenRepository;
import com.seob.systemdomain.auth.service.JwtProvider;
import com.seob.systemdomain.user.domain.PasswordHasher;
import com.seob.systemdomain.user.domain.UserDomain;
import com.seob.systemdomain.user.domain.vo.Email;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.user.repository.UserRepository;
import com.seob.systeminfra.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailVerificationRepository emailVerficationRepository;
    private final EmailService emailService;

    public UserId register(String email, String password, String nickname){

        if(userRepository.existsByEmail(Email.from(email))){
            throw AlreadyExistsEmailException.EXCEPTION;
        }

        UserDomain user = UserDomain.create(email,nickname,password,passwordHasher);

        userRepository.save(user);

        //인증코드 생성 및 발송
        String verificationCode = emailVerficationRepository.generateVerificationCode();
        emailVerficationRepository.saveVerificationCode(user.getEmail(), verificationCode, null);

        emailService.sendVerificationEmail(user.getEmail().getValue(), verificationCode);

        return user.getUserId();
    }

    public VerificationServiceResponse verifyEmail(String email, String code){
        Email userEmail = Email.from(email);

        if(!emailVerficationRepository.verifyCode(userEmail,code)){
            throw InvalidCodeException.EXCEPTION;
        }

        UserDomain user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);

        user.activate();
        userRepository.save(user);

        emailVerficationRepository.deleteVerificationCode(userEmail);

        return VerificationServiceResponse.of();

    }

    public ResendCodeServiceResponse resendVerificationCode(String email){
        Email userEmail = Email.from(email);

        UserDomain user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> AlreadyExistsEmailException.EXCEPTION); //예외 추가

        if(user.isActive()){
            throw AlreadyVerifiedException.EXCEPTION;
        }

        emailVerficationRepository.deleteVerificationCode(userEmail);

        String verificationCode = emailVerficationRepository.generateVerificationCode();
        emailVerficationRepository.saveVerificationCode(userEmail, verificationCode, null);
        emailService.sendVerificationEmail(user.getEmail().getValue(), verificationCode);

        return ResendCodeServiceResponse.of();
    }

    public AuthServiceResponse login(String email, String password){

        UserDomain user = userRepository.findByEmail(Email.from(email))
                .orElseThrow(() -> new UsernameNotFoundException(email));

        if(!user.isActive()){
            throw UserNotActiveException.EXCEPTION;
        }

        if(!passwordHasher.matches(password, user.getPassword().getEncodedValue())){
            throw WrongPasswordException.EXCEPTION;
        }

        //jwt 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(user.getUserId());
        String refreshToken = jwtProvider.generateRefreshToken(user.getUserId());

        refreshTokenRepository.saveRefreshToken(user.getUserId(),refreshToken, jwtProvider.getRefreshTokenValidity());

        return AuthServiceResponse.of(accessToken, refreshToken, jwtProvider.getAccessTokenValidity());
    }

    public AuthServiceResponse refresh(String refreshToken){

        if(!jwtProvider.validateToken(refreshToken)){
            throw new BadCredentialsException("만료되거나 유효하지 않은 리프레시 토큰입니다.");
        }

        UserId userId = jwtProvider.getUserIdFromToken(refreshToken);

        if(!refreshTokenRepository.validateRefreshToken(userId, refreshToken)){
            throw new BadCredentialsException("만료되거나 유효하지 않은 리프레시 토큰입니다.");
        }

        String newAccessToken = jwtProvider.generateAccessToken(userId);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);

        refreshTokenRepository.saveRefreshToken(userId,newRefreshToken,jwtProvider.getRefreshTokenValidity());

        return AuthServiceResponse.of(newAccessToken, newRefreshToken, jwtProvider.getAccessTokenValidity());
    }

    public void logout(UserId userId){
        refreshTokenRepository.deleteRefreshToken(userId);
    }



}
