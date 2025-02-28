package com.seob.systemdomain.user.repository;

import com.seob.systemdomain.user.domain.UserDomain;
import com.seob.systemdomain.user.domain.vo.Email;

import java.util.Optional;

public interface UserRepository {

    UserDomain save(UserDomain userDomain);

    Optional<UserDomain> findByEmail(Email email);

    boolean existsByEmail(Email email);

}
