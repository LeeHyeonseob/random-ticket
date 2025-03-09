package com.seob.systemdomain.user.domain;

public interface PasswordHasher {
    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
