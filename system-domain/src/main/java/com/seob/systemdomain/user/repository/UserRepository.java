package com.seob.systemdomain.user.repository;

import com.seob.systemdomain.user.domain.UserDomain;
import com.seob.systemdomain.user.domain.vo.Email;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.user.dto.UserProfileInfo;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    UserDomain save(UserDomain userDomain);

    Optional<UserDomain> findByEmail(Email email);

    Optional<UserDomain> findById(UserId userId);

    boolean existsByEmail(Email email);
    
    //사용자 ID로 프로필 정보 조회
    Optional<UserProfileInfo> findProfileById(UserId userId);
    
    //이름과 이메일로 사용자 찾기
    List<UserId> findByNameAndEmail(String name, String email);
}
