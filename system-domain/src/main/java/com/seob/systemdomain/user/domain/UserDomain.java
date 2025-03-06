package com.seob.systemdomain.user.domain;

import com.seob.systemdomain.user.domain.vo.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDomain {

    private UserId userId;

    private Email email;

    private Nickname nickname;

    private Password password;

    private UserRole role;

    private boolean active;



    public static UserDomain create(String email, String nickname, String password, PasswordEncoder passwordEncoder){
        UserDomain user = new UserDomain();
        user.userId = UserId.create();
        user.email = Email.from(email);
        user.nickname = Nickname.of(nickname);
        user.password = Password.encode(password,passwordEncoder);
        user.role = UserRole.USER;
        user.active = false;

        return user;
    }

    public static UserDomain of(String userid, String email, String nickname, String encodedPassword, UserRole role, boolean active){
        UserDomain user = new UserDomain();
        user.userId = new UserId(userid);
        user.email = Email.from(email);
        user.nickname = Nickname.of(nickname);
        user.password = Password.of(encodedPassword);
        user.role = role;
        user.active = active;

        return user;
    }

    public void changePassword(String oldPassword, String newPassword, PasswordEncoder passwordEncoder){
        password.matches(oldPassword,passwordEncoder);
        password = Password.encode(newPassword,passwordEncoder);
    }

    public void changeEmail(String newEmail){
        email = Email.from(newEmail);
    }

    public void changeNickname(String newNickname){
        nickname = Nickname.of(newNickname);
    }

    //관리자전환
    public void toAdmin(){
        role = UserRole.ADMIN;
    }

    //유저 전환
    public void toUser(){
        role = UserRole.USER;
    }


    //관리자 여부
    public boolean isAdmin(){
        return role == UserRole.ADMIN;
    }

    //계정 활성화
    public void activate(){
        this.active = true;
    }




}
