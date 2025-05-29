package com.seob.systeminfra.user.entity;

import com.seob.systemdomain.user.domain.vo.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    private String userId;

    private String email;

    private String nickname;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Setter(AccessLevel.NONE)
    private boolean active;

    public UserEntity (String userId, String email, String nickname, String password, UserRole role, boolean active) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
        this.active = active;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
