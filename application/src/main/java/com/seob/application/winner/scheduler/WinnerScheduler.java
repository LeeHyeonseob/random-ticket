package com.seob.application.winner.scheduler;

import com.seob.application.winner.service.WinnerFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class WinnerScheduler {

    private final WinnerFacadeService winnerFacadeService;

    @Scheduled(cron = "0 1 0 * * *")
    public void selectAndSendRewardsEachDay(){
        winnerFacadeService.processYesterdayEvent();
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void retry(){
        winnerFacadeService.retryFailedRewards();
    }
}
