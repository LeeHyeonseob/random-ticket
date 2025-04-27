package com.seob.systemdomain.user.service;

import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.user.dto.UserProfileInfo;

//사용자 서비스 인터페이스
public interface UserService {

    //사용자 프로필 정보 업데이트
    UserProfileInfo updateProfile(UserId userId, String nickname);
    
    //사용자 비밀번호 변경
    boolean changePassword(UserId userId, String currentPassword, String newPassword);
}
