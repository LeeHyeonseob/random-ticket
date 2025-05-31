package com.seob.application.winner.scheduler;

import com.seob.application.winner.service.WinnerFacadeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class WinnerScheduler {

    private final WinnerFacadeService winnerFacadeService;

    // 당첨자 선정 후 보상 보내기
    @Scheduled(cron = "0 0 1 * * *")
    public void selectAndSendRewardsEachDay(){
        winnerFacadeService.processYesterdayEvent();
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void retry(){
        int failedCount = winnerFacadeService.retryFailedRewards();
        log.info("failedCount:{}", failedCount);
    }
}
