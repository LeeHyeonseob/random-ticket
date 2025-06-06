package com.seob.systemdomain.user.domain;

import com.seob.systemdomain.user.domain.vo.UserRole;
import com.seob.systemdomain.user.exception.PasswordMismatchException;
import com.seob.systemdomain.user.exception.UserNotActiveException;
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
    private PasswordHasher mockPasswordHasher;

    @Test
    @DisplayName("User 생성")
    void create_UserDomain() {
        //given
        String email = "hyeonseob22@gmail.com";
        String rawPassword = "Password123!";
        String nickname = "nickname";

        given(mockPasswordHasher.encode(rawPassword)).willReturn("encoded" + rawPassword);
        //when
        UserDomain userDomain = UserDomain.create(email,nickname, rawPassword, mockPasswordHasher);

        //then
        assertNotNull(userDomain);
        assertThat(userDomain.getUserId()).isNotNull();
        assertThat(userDomain.getEmail().getValue()).isEqualTo("hyeonseob22@gmail.com");
        assertThat(userDomain.getPassword().getEncodedValue()).isEqualTo("encodedPassword123!");
        assertThat(userDomain.getRole().toString()).isEqualTo("USER");
        assertThat(userDomain.isActive()).isFalse();

    }

    @Test
    @DisplayName("비밀번호 변경")
    void changePassword() {

        //given
        String email = "hyeonseob22@gmail.com";
        String rawPassword = "Password123!";
        String oldPassword = "Password123!";
        String newPassword = "Password123@";
        String nickname = "nickname";


        given(mockPasswordHasher.encode(rawPassword)).willReturn("encoded" + rawPassword);
        UserDomain userDomain = UserDomain.create(email, nickname, rawPassword, mockPasswordHasher);

        given(mockPasswordHasher.matches(rawPassword, "encoded" + rawPassword)).willReturn(true);
        given(mockPasswordHasher.encode(newPassword)).willReturn("encoded" + newPassword);

        //when
        userDomain.changePassword(rawPassword, newPassword, mockPasswordHasher);

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
        String nickname = "nickname";


        given(mockPasswordHasher.encode(rawPassword)).willReturn("encoded" + rawPassword);
        UserDomain userDomain = UserDomain.create(email, nickname, rawPassword, mockPasswordHasher);

        // 기존 비밀번호 매칭 실패
        given(mockPasswordHasher.matches(wrongOldPassword, "encoded" + rawPassword)).willReturn(false);

        // when & then
        assertThrows(PasswordMismatchException.class,
                () -> userDomain.changePassword(wrongOldPassword, newPassword, mockPasswordHasher));
    }

    @Test
    @DisplayName("관리자 전환 및 사용자 전환")
    void roleChange() {
        // given
        String email = "hyeonseob22@gmail.com";
        String rawPassword = "Password123!";
        String nickname = "nickname";


        given(mockPasswordHasher.encode(rawPassword)).willReturn("encoded" + rawPassword);
        UserDomain userDomain = UserDomain.create(email, nickname, rawPassword, mockPasswordHasher);

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
        String nickname = "nickname";


        given(mockPasswordHasher.encode(rawPassword)).willReturn("encoded" + rawPassword);
        UserDomain userDomain = UserDomain.create(email, nickname, rawPassword, mockPasswordHasher);

        assertThat(userDomain.isActive()).isFalse();

        userDomain.activate();

        assertThat(userDomain.isActive()).isTrue();


    }

    @Test
    @DisplayName("활성화 상태 검증 성공")
    void validateActive_Success(){
        //given
        String email = "hyeonseob22@gmail.com";
        String rawPassword = "Password123!";
        String nickname = "nickname";
        given(mockPasswordHasher.encode(rawPassword)).willReturn("encoded" + rawPassword);
        UserDomain userDomain = UserDomain.create(email, nickname, rawPassword, mockPasswordHasher);
        userDomain.activate();

        //when && then
        assertDoesNotThrow(userDomain::validateActive);


    }

    @Test
    @DisplayName("활성화 상태 검증 실패")
    void validateActive_Failure(){
        //given
        String email = "hyeonseob22@gmail.com";
        String rawPassword = "Password123!";
        String nickname = "nickname";
        given(mockPasswordHasher.encode(rawPassword)).willReturn("encoded" + rawPassword);
        UserDomain userDomain = UserDomain.create(email, nickname, rawPassword, mockPasswordHasher);


        //when && then
        assertThrows(UserNotActiveException.class, () -> userDomain.validateActive());
    }

    @Test
    @DisplayName("이메일 변경")
    void changeEmail(){
        //given
        String email = "hyeonseob22@gmail.com";
        String rawPassword = "Password123!";
        String nickname = "nickname";
        given(mockPasswordHasher.encode(rawPassword)).willReturn("encoded" + rawPassword);
        UserDomain userDomain = UserDomain.create(email, nickname, rawPassword, mockPasswordHasher);

        //when
        userDomain.changeEmail(email);

        //when && then
        assertThat(userDomain.getEmail().getValue()).isEqualTo(email);
    }




}