package com.seob.systemdomain.winner.repository;

import com.seob.systemdomain.winner.dto.WinnerDetailInfo;
import com.seob.systemdomain.winner.dto.WinnerUserDetailInfo;
import com.seob.systemdomain.winner.vo.RewardStatus;

import java.util.List;

public interface WinnerQueryRepository {

    List<WinnerDetailInfo> findDetailsByEventId(Long eventId);


    List<WinnerDetailInfo> findDetailsByStatus(RewardStatus status);


    List<WinnerDetailInfo> findAllDetails();


    List<WinnerUserDetailInfo> findUserDetailsByUserId(String userId);
}
