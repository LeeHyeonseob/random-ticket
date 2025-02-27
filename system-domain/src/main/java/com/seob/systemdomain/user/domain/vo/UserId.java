package com.seob.systemdomain.user.domain.vo;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

@Value
@Getter
@EqualsAndHashCode
public class UserId implements Serializable {

    private String value;

    public static UserId of(String value) {
        return new UserId(value);
    }

    public static UserId create(){
        return new UserId(UUID.randomUUID().toString());
    }

}
