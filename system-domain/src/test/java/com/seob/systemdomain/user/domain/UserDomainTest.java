package com.seob.systemdomain.user.domain;

import com.seob.systemdomain.user.domain.vo.UserRole;
import com.seob.systemdomain.user.exception.PasswordMismatchException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserDomainTest {

    @Mock
    private PasswordEncoder mockPasswordEncoder;

    @Test
    @DisplayName("User 생성")
    void create_UserDomain() {
        //given
        String email = "hyeonseob22@gmail.com";
        String rawPassword = "Password123!";

        given(mockPasswordEncoder.encode(rawPassword)).willReturn("encoded" + rawPassword);
        //when
        UserDomain userDomain = UserDomain.create(email, rawPassword, mockPasswordEncoder);

        //then
        assertNotNull(userDomain);
        assertThat(userDomain.getUserId()).isNotNull();
        assertThat(userDomain.getEmail().getValue()).isEqualTo("hyeonseob22@gmail.com");
        assertThat(userDomain.getPassword().getEncodedValue()).isEqualTo("encodedPassword123!");
        assertThat(userDomain.getRole().toString()).isEqualTo("USER");
        assertThat(userDomain.getActive()).isFalse();

    }

    @Test
    @DisplayName("비밀번호 변경")
    void changePassword() {

        //given
        String email = "hyeonseob22@gmail.com";
        String rawPassword = "Password123!";
        String oldPassword = "Password123!";
        String newPassword = "Password123@";

        given(mockPasswordEncoder.encode(rawPassword)).willReturn("encoded" + rawPassword);
        UserDomain userDomain = UserDomain.create(email, rawPassword, mockPasswordEncoder);

        given(mockPasswordEncoder.matches(rawPassword, "encoded" + rawPassword)).willReturn(true);
        given(mockPasswordEncoder.encode(newPassword)).willReturn("encoded" + newPassword);

        //when
        userDomain.changePassword(rawPassword, newPassword, mockPasswordEncoder);

        //then
        assertThat(userDomain.getPassword().getEncodedValue()).isEqualTo("encodedPassword123@");


    }

    @Test
    @DisplayName("비밀번호 변경 실패")
    void changePassword_failure() {
        // given
        String email = "hyeonseob22@gmail.com";
        String rawPassword = "Password123!";
        String wrongOldPassword = "WrongPassword!";
        String newPassword = "Password123@";

        given(mockPasswordEncoder.encode(rawPassword)).willReturn("encoded" + rawPassword);
        UserDomain userDomain = UserDomain.create(email, rawPassword, mockPasswordEncoder);

        // 기존 비밀번호 매칭 실패
        given(mockPasswordEncoder.matches(wrongOldPassword, "encoded" + rawPassword)).willReturn(false);

        // when & then
        assertThrows(PasswordMismatchException.class,
                () -> userDomain.changePassword(wrongOldPassword, newPassword, mockPasswordEncoder));
    }

    @Test
    @DisplayName("관리자 전환 및 사용자 전환")
    void roleChange() {
        // given
        String email = "hyeonseob22@gmail.com";
        String rawPassword = "Password123!";

        given(mockPasswordEncoder.encode(rawPassword)).willReturn("encoded" + rawPassword);
        UserDomain userDomain = UserDomain.create(email, rawPassword, mockPasswordEncoder);

        // 기본적으로 USER 역할임
        assertThat(userDomain.getRole()).isEqualTo(UserRole.USER);

        // when - 관리자 전환
        userDomain.toAdmin();
        // then
        assertThat(userDomain.getRole()).isEqualTo(UserRole.ADMIN);
        assertTrue(userDomain.isAdmin());

        // when - 유저로 전환
        userDomain.toUser();
        // then
        assertThat(userDomain.getRole()).isEqualTo(UserRole.USER);
        assertFalse(userDomain.isAdmin());
    }

    @Test
    @DisplayName("계정 활성화")
    void activate_UserDomain() {
        //given
        String email = "hyeonseob22@gmail.com";
        String rawPassword = "Password123!";

        given(mockPasswordEncoder.encode(rawPassword)).willReturn("encoded" + rawPassword);
        UserDomain userDomain = UserDomain.create(email, rawPassword, mockPasswordEncoder);

        assertThat(userDomain.isActive()).isFalse();

        userDomain.activate();

        assertThat(userDomain.isActive()).isTrue();


    }
}