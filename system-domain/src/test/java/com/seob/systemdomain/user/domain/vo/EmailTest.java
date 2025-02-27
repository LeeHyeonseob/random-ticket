package com.seob.systemdomain.user.domain.vo;

import com.seob.systemdomain.user.exception.InvalidEmailFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    @DisplayName("정상적인 이메일을 넣으면 Email 생성")
    void validEmailTest() {
        //given
        String emailText = "hyeonseob22@gmail.com";

        //when
        Email email = Email.from(emailText);

        //then
        assertThat(email.getValue()).isEqualTo("hyeonseob22@gmail.com");
    }

    @Test
    @DisplayName("유효하지 않은 이메일이면 예외가 발생_1")
    void invalidEmailTest_1() {
        //given
        String emailText = "hyeonseob22gmail.com";

        // when & then
        assertThrows(InvalidEmailFormatException.class, () -> Email.from(emailText));


    }

    @Test
    @DisplayName("유효하지 않은 이메일이면 예외가 발생_2")
    void invalidEmailTest_2() {
        //given
        String emailText = "hyeonseob22@gmail.c";

        // when & then
        assertThrows(InvalidEmailFormatException.class, () -> Email.from(emailText));
    }

    @Test
    @DisplayName("유효하지 않은 이메일이면 예외가 발생_3")
    void invalidEmailTest_3() {
        //given
        String emailText = " @gmail.com";
        // when & then
        assertThrows(InvalidEmailFormatException.class, () -> Email.from(emailText));

    }

    @Test
    @DisplayName("유효하지 않은 이메일이면 예외가 발생_4")
    void invalidEmailTest_4() {
        //given
        String emailText = " hyeonseob@-gmail.com";
        // when & then
        assertThrows(InvalidEmailFormatException.class, () -> Email.from(emailText));

    }

    @Test
    @DisplayName("이메일이 100자가 넘어갈 경우 예외가 발생")
    void EmailOver100Test(){

        //given
        String longEmailTest = "h".repeat(101) + "@gmail.com";

        // when & then
        assertThrows(InvalidEmailFormatException.class, () -> Email.from(longEmailTest));


    }

    @Test
    @DisplayName("같은 이메일 값이라면 equals/hashCode 결과 동일")
    void equalsAndHashCodeTest() {
        // given
        String emailValue = "hyeonseob22@gmail.com";

        // when
        Email email1 = Email.from(emailValue);
        Email email2 = Email.from(emailValue);

        // then
        assertThat(email1).isEqualTo(email2);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
    }


}