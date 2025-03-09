package com.seob.systemdomain.user.domain.vo;

import com.seob.systemdomain.user.domain.PasswordHasher;
import com.seob.systemdomain.user.exception.InvalidPasswordFormatException;
import com.seob.systemdomain.user.exception.PasswordMismatchException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)

class PasswordTest {

    @Mock
    private PasswordHasher mockPasswordHasher;


    @Test
    @DisplayName("encode메서드로 Password 객체 생성")
    void validPassword() {
        // given
        String rawPassword = "Password123!";

        given(mockPasswordHasher.encode(rawPassword)).willReturn("encoded" + rawPassword);

        // when
        Password password = Password.encode(rawPassword, mockPasswordHasher);

        // then
        assertThat(password).isNotNull();
        assertThat(password.getEncodedValue()).isEqualTo("encodedPassword123!");
    }

    @Test
    @DisplayName("encode메서드로 유효하지않은 비밀번호면 예외 발생")
    void encode_invalidPassword_throwException() {
        // given - 여러 케이스를 배열로 묶어 테스트해도 됨
        String[] invalidPasswords = {
                null,                 // null
                "",                   // 빈 문자열
                "abc123!",            // 대문자 빠짐
                "ABC123!",            // 소문자 빠
                "Abcdefg!",           // 숫자 빠짐
                "Abc123ab",           // 특수문자 빠짐
                "Abc123!@aaaaaaaaaaaaa" // 21자 이상(정규식 8~20자 범위 초과 가정)
        };

        for (String invalid : invalidPasswords) {
            assertThrows(InvalidPasswordFormatException.class,
                    () -> Password.encode(invalid, mockPasswordHasher),
                    "Should throw InvalidPasswordFormatException for password: " + invalid
            );
        }
    }

    @Test
    @DisplayName("of 메서드로 인코딩된 값 불러오기")
    void of_withEncodedValue_success() {
        // given
        String encodedPassword = "alreadyEncodedValue!";

        // when
        Password password = Password.of(encodedPassword);

        // then
        assertThat(password).isNotNull();
        assertThat(password.getEncodedValue()).isEqualTo(encodedPassword);
    }

    @Test
    @DisplayName("matches()메서드로 비밀번호 비교하기")
    void matches_success() {
        // given
        String rawPassword = "Password123!";
        String encodedPassword = "encodedPassword123!";
        Password password = Password.of(encodedPassword);

        given(mockPasswordHasher.matches(rawPassword, encodedPassword)).willReturn(true);

        // when & then (예외가 발생하지 않으면 성공)
        password.matches(rawPassword, mockPasswordHasher);
    }

    @Test
    @DisplayName("matches()메서드로 불일치시 예외 발생")
    void matches_fail_throwException() {
        // given
        String rawPassword = "Password123!";
        String encodedPassword = "encodedPassword123!";
        Password password = Password.of(encodedPassword);

        given(mockPasswordHasher.matches(rawPassword, encodedPassword)).willReturn(false);

        // when & then
        assertThrows(PasswordMismatchException.class,
                () -> password.matches(rawPassword, mockPasswordHasher));
    }

}