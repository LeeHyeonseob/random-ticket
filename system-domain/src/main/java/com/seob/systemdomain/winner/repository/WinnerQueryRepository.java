package com.seob.systemdomain.winner.repository;

import com.seob.systemdomain.winner.dto.WinnerNotificationInfo;
import com.seob.systemdomain.winner.dto.WinnerRewardDetailInfo;
import com.seob.systemdomain.winner.dto.WinnerUserDetailInfo;
import com.seob.systemdomain.winner.vo.RewardStatus;

import java.util.List;
import java.util.Optional;

public interface WinnerQueryRepository {

    List<WinnerRewardDetailInfo> findDetailsByEventId(Long eventId);
    
    List<WinnerRewardDetailInfo> findDetailsByStatus(RewardStatus status);
    
    List<WinnerRewardDetailInfo> findAllDetails();
    
    List<WinnerUserDetailInfo> findUserDetailsByUserId(String userId);

    boolean existsByUserNameAndEmail(String userName, String email);

    Optional<WinnerNotificationInfo> findNotificationInfoById(Long winnerId);
}
