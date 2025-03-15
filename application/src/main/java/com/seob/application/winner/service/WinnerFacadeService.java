package com.seob.application.winner.service;

import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.winner.domain.WinnerDomain;
import com.seob.systemdomain.winner.dto.WinnerDetailInfo;
import com.seob.systemdomain.winner.vo.RewardStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WinnerFacadeService {

    private final WinnerApplicationService winnerApplicationService;
    private final EventRepository eventRepository;

    //당첨자 선정 및 보상 발송
    public boolean processEventWinnerWithReward(Long eventId){
        //당첨자 선정
        WinnerDomain winner = winnerApplicationService.selectWinner(eventId);

        //보상 발송
        return winnerApplicationService.sendReward(winner.getId());

    }



    // 종료된 모든 이벤트에 대해 당첨자 선정 및 보상 발송 처리

    public void processYesterdayEvent() {

        LocalDate yesterday = LocalDate.now().minusDays(1);


        Long yesterdayEventId = eventRepository.findIdByDate(yesterday);

        if(yesterday != null){
            try{
                processEventWinnerWithReward(yesterdayEventId);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }



    // 보상 발송에 실패한 당첨자들에게 보상을 재발송합니다. 그 후 재발송에 성공한 숫자를 반환합니다
    public int retryFailedRewards(){
        List<WinnerDetailInfo> failedWinners = winnerApplicationService.getWinnersByStatus(RewardStatus.FAILED);

        if(failedWinners.isEmpty()){
            return 0;
        }

        int successCount = 0;
        for(WinnerDetailInfo reWinner : failedWinners){
            try{
                boolean success = winnerApplicationService.sendReward(reWinner.winnerId());
                if(success){
                    successCount++;
                }
            }catch(Exception e){
                continue;
            }
        }

        return successCount;

    }
}
