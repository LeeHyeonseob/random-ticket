package com.seob.application.winner.service;

import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.winner.domain.WinnerDomain;
import com.seob.systemdomain.winner.dto.WinnerRewardDetailInfo;
import com.seob.systemdomain.winner.vo.RewardStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WinnerFacadeService {

    private final WinnerApplicationService winnerApplicationService;
    private final EventRepository eventRepository;

    //당첨자 선정 및 보상 발송
    public boolean processEventWinnerWithReward(Long eventId){
        log.info("이벤트 당첨자 선정 및 보상 발송 처리: {}", eventId);
        
        //당첨자 선정
        WinnerDomain winner = winnerApplicationService.selectWinner(eventId);
        log.info("이벤트 당첨자 선정 완료 - 이벤트 ID: {}, 당첨자 ID: {}", eventId, winner.getId());

        //보상 발송
        boolean rewardSent = winnerApplicationService.sendReward(winner.getId());
        
        if(rewardSent) {
            log.info("당첨자 보상 발송 성공 - 당첨자 ID: {}, 이벤트 ID: {}", winner.getId(), eventId);
        } else {
            log.warn("당첨자 보상 발송 실패 - 당첨자 ID: {}, 이벤트 ID: {}", winner.getId(), eventId);
        }
        
        return rewardSent;
    }

    // 종료된 모든 이벤트에 대해 당첨자 선정 및 보상 발송 처리
    public void processYesterdayEvent() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("어제 날짜 이벤트 처리 시작: {}", yesterday);

        Long yesterdayEventId = eventRepository.findIdByDate(yesterday);

        if(yesterdayEventId != null){
            try{
                log.info("이벤트 당첨자 선정 및 보상 발송 처리 시작: {}", yesterdayEventId);
                processEventWinnerWithReward(yesterdayEventId);
                log.info("이벤트 당첨자 선정 및 보상 발송 처리 완료: {}", yesterdayEventId);
            }catch(Exception e){
                log.error("이벤트 당첨자 선정 및 보상 발송 처리 실패 - 이벤트 ID: {}, 날짜: {}", 
                    yesterdayEventId, yesterday, e);
            }
        } else {
            log.info("해당 날짜에 이벤트가 없습니다: {}", yesterday);
        }
    }

    // 보상 발송에 실패한 당첨자들에게 보상을 재발송합니다. 그 후 재발송에 성공한 숫자를 반환합니다
    public int retryFailedRewards(){
        log.info("실패한 보상 재발송 처리 시작");
        
        List<WinnerRewardDetailInfo> failedWinners = winnerApplicationService.getWinnersByStatus(RewardStatus.FAILED);

        if(failedWinners.isEmpty()){
            log.info("재발송할 실패한 보상이 없습니다");
            return 0;
        }

        log.info("재발송할 실패한 보상 {}개 발견", failedWinners.size());
        int successCount = 0;
        
        for(WinnerRewardDetailInfo reWinner : failedWinners){
            try{
                log.debug("당첨자 보상 재발송 시도: {}", reWinner.winnerId());
                boolean success = winnerApplicationService.sendReward(reWinner.winnerId());
                if(success){
                    successCount++;
                    log.debug("당첨자 보상 재발송 성공: {}", reWinner.winnerId());
                }
            }catch(Exception e){
                log.error("당첨자 보상 재발송 실패: {}", reWinner.winnerId(), e);
                continue;
            }
        }

        log.info("보상 재발송 처리 완료. 성공: {}/{}", successCount, failedWinners.size());
        return successCount;
    }
}
