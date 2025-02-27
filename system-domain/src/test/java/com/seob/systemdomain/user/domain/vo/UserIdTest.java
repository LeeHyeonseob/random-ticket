package com.seob.systemdomain.user.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserIdTest {

    @Test
    @DisplayName("of 메서드로 주어진 값을 불러오기")
    void ofUserIdTest(){
        //given
        String id = "111111111111";

        //when
        UserId userId = UserId.of(id);

        //then
        assertThat(userId).isNotNull();
        assertThat(userId.getValue()).isEqualTo("111111111111");
    }

    @Test
    @DisplayName("create 메서드로 랜덤 UUID 생성")
    void createUserIdTest(){
        //when
        UserId userId = UserId.create();

        //then
        assertThat(userId).isNotNull();
        assertThat(userId.getValue()).isNotBlank();

//        System.out.println(userId.toString());

    }

    @Test
    @DisplayName("객체 비교 테스트")
    void equals_and_hashCode_Test() {
        // given
        UserId userId1 = UserId.of("111111111111");
        UserId userId2 = UserId.of("111111111111");
        UserId userId3 = UserId.of("222222222222");

        // when & then
        assertThat(userId1).isEqualTo(userId2);
        assertThat(userId1).isNotEqualTo(userId3);

        assertThat(userId1.hashCode()).isEqualTo(userId2.hashCode());
        assertThat(userId1.hashCode()).isNotEqualTo(userId3.hashCode());
    }

}