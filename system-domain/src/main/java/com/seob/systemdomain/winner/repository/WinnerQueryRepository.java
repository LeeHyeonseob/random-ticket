package com.seob.systemdomain.winner.repository;

import com.seob.systemdomain.winner.dto.WinnerRewardDetailInfo;
import com.seob.systemdomain.winner.dto.WinnerUserDetailInfo;
import com.seob.systemdomain.winner.vo.RewardStatus;

import java.util.List;

public interface WinnerQueryRepository {
    // WinnerDetailInfo를 WinnerRewardDetailInfo로 대체
    List<WinnerRewardDetailInfo> findDetailsByEventId(Long eventId);
    
    List<WinnerRewardDetailInfo> findDetailsByStatus(RewardStatus status);
    
    List<WinnerRewardDetailInfo> findAllDetails();
    
    List<WinnerUserDetailInfo> findUserDetailsByUserId(String userId);

    boolean existsByUserNameAndEmail(String userName, String email);
}
