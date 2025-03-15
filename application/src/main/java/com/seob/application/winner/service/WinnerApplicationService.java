package com.seob.application.winner.service;


import com.seob.systemdomain.winner.domain.WinnerDomain;
import com.seob.systemdomain.winner.dto.WinnerDetailInfo;
import com.seob.systemdomain.winner.dto.WinnerUserDetailInfo;
import com.seob.systemdomain.winner.vo.RewardStatus;

import java.util.List;

public interface WinnerApplicationService {

    WinnerDomain selectWinner(Long eventId);

    boolean sendReward(Long winnerId);

    //조회
    List<WinnerUserDetailInfo> getWinnersByUserId(String userId);

    List<WinnerDetailInfo> getWinnersByEventId(Long eventId);

    List<WinnerDetailInfo> getAllWinners();

    List<WinnerDetailInfo> getWinnersByStatus(RewardStatus status);

}
