package com.seob.systemdomain.user.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

@Getter
@EqualsAndHashCode
public class Nickname {
    private final String value;

    private Nickname(String value) {
        this.value = value;
    }

    public static Nickname of(String value) {
        return new Nickname(value);
    }

}
