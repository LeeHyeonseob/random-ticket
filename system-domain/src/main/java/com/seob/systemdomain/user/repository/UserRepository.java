package com.seob.systemdomain.user.repository;

import com.seob.systemdomain.user.domain.UserDomain;
import com.seob.systemdomain.user.domain.vo.Email;
import com.seob.systemdomain.user.domain.vo.UserId;

import java.util.Optional;

public interface UserRepository {

    UserDomain save(UserDomain userDomain);

    Optional<UserDomain> findByEmail(Email email);

    Optional<UserDomain> findById(UserId userId);

    boolean existsByEmail(Email email);



}
