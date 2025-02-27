package com.seob.systemdomain.user.domain.vo;

import com.seob.systemdomain.user.domain.PasswordEncoder;
import com.seob.systemdomain.user.exception.InvalidPasswordFormatException;
import com.seob.systemdomain.user.exception.PasswordMismatchException;
import lombok.Getter;

import java.util.regex.Pattern;

@Getter
public class Password {
    public static final String REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@!%*?&])[A-Za-z\\d$@!%*?&]{8,20}";
    private static final Pattern PATTERN = Pattern.compile(REGEX); // 정적 필드로 사용 -> 대량 검증시 성능 저하 덜함

    private final String encodedValue;

    private Password(String encodedValue) {
        this.encodedValue = encodedValue;
    }

    public static Password encode(String rawPassword, PasswordEncoder passwordEncoder) {
        validate(rawPassword);

        String encodedPassword = passwordEncoder.encode(rawPassword);

        return new Password(encodedPassword);
    }

    private static void validate(String rawPassword){
        if(rawPassword == null || !PATTERN.matcher(rawPassword).matches()){
            throw InvalidPasswordFormatException.EXCEPTION;
        }
    }

    public void matches(String rawPassword, PasswordEncoder passwordEncoder){
        if(!passwordEncoder.matches(rawPassword, encodedValue)){
            throw PasswordMismatchException.EXCEPTION;
        }
    }

    public static Password of(String encodedPassword){
        return new Password(encodedPassword);
    }





}
