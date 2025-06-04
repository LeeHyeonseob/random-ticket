package com.seob.application.winner.service;

import com.seob.systemdomain.winner.dto.WinnerNotificationInfo;
import com.seob.systemdomain.winner.dto.WinnerPublicInfo;
import com.seob.systemdomain.winner.dto.WinnerRewardDetailInfo;
import com.seob.systeminfra.email.EmailService;
import com.seob.application.winner.exception.AlreadyWinnerExistsException;
import com.seob.application.winner.exception.EntryNotFoundException;
import com.seob.application.winner.exception.NoRewardInEventException;
import com.seob.application.winner.exception.WinnerNotFoundException;
import com.seob.systemdomain.entry.repository.EntryRepository;
import com.seob.systemdomain.reward.repository.RewardRepository;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.winner.domain.WinnerDomain;
import com.seob.systemdomain.winner.dto.WinnerUserDetailInfo;
import com.seob.systemdomain.winner.repository.WinnerQueryRepository;
import com.seob.systemdomain.winner.service.WinnerService;
import com.seob.systemdomain.winner.vo.RewardStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class WinnerApplicationServiceImpl implements WinnerApplicationService {

    private final WinnerService winnerService;
    private final WinnerQueryRepository winnerQueryRepository;
    private final EntryRepository entryRepository;
    private final RewardRepository rewardRepository;
    private final EmailService emailService;




    @Override
    public WinnerDomain selectWinner(Long eventId) {
        //이벤트 참가자 조회
        List<String> participantIds = entryRepository.findUserIdByEventId(eventId);

        //이벤트 참가자가 없으면..
        if(participantIds.isEmpty()) {
            //참가자 없음 예외 처리
            throw EntryNotFoundException.EXCEPTION;
        }
        //이미 당첨자 있으면
        if(winnerService.existsByEventId(eventId)){
            //이미 당첨되었으면 예외처리
            throw AlreadyWinnerExistsException.EXCEPTION;
        }

        //랜덤 추첨
        Random random = new Random();
        String selectedUserId = participantIds.get(random.nextInt(participantIds.size()));

        Long rewardId = rewardRepository.findByEventId(eventId)
                .orElseThrow(() -> NoRewardInEventException.EXCEPTION)
                .getId();

        return winnerService.createWinner(UserId.of(selectedUserId), eventId, rewardId);
    }

    @Override
    public boolean sendReward(Long winnerId) {
        try {
            executeRewardSend(winnerId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void sendRewardManually(Long winnerId) {
        try {
            executeRewardSend(winnerId);
        } catch (Exception e) {
            throw new RuntimeException("보상 발송 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private void executeRewardSend(Long winnerId) {

        WinnerNotificationInfo winnerNotificationInfo = winnerQueryRepository.findNotificationInfoById(winnerId)
                .orElseThrow(() -> WinnerNotFoundException.EXCEPTION);


        if (winnerNotificationInfo.status() != RewardStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 당첨자입니다. 현재 상태: " + winnerNotificationInfo.status());
        }

        try {

            emailService.sendRewardEmail(
                    winnerNotificationInfo.userEmail(),
                    winnerNotificationInfo.eventName(),
                    winnerNotificationInfo.rewardUrl()
            );

            winnerService.updateStatus(winnerNotificationInfo.winnerId(), RewardStatus.COMPLETE);

        } catch (Exception e) {
            winnerService.updateStatus(winnerNotificationInfo.winnerId(), RewardStatus.FAILED);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<WinnerUserDetailInfo> getWinnersByUserId(String userId) {
        return winnerQueryRepository.findUserDetailsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WinnerRewardDetailInfo> getWinnersByEventId(Long eventId) {
        return winnerQueryRepository.findDetailsByEventId(eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WinnerRewardDetailInfo> getAllWinners() {
        return winnerQueryRepository.findAllDetails();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WinnerRewardDetailInfo> getWinnersByStatus(RewardStatus status) {
        return winnerQueryRepository.findDetailsByStatus(status);
    }
    
    //이름과 이메일로 당첨자 찾기
    @Override
    @Transactional(readOnly = true)
    public boolean checkWinner(String name, String email) {
        return winnerQueryRepository.existsByUserNameAndEmail(name, email);
    }
    
    //현재 로그인한 사용자의 당첨 내역 조회
    @Override
    @Transactional(readOnly = true)
    public List<WinnerUserDetailInfo> getMyWinners(UserId userId) {
        return winnerQueryRepository.findUserDetailsByUserId(userId.getValue());
    }
    
    //공개 당첨자 목록 조회 (마스킹 처리) - 보상 정보를 포함하여 한 번에 조회
    @Override
    @Transactional(readOnly = true)
    public List<WinnerPublicInfo> getPublicWinners(Long eventId) {
        List<WinnerRewardDetailInfo> winnersWithReward;
        
        if (eventId != null) {
            winnersWithReward = winnerQueryRepository.findDetailsByEventId(eventId);
        } else {
            winnersWithReward = winnerQueryRepository.findAllDetails();
        }
        
        // 당첨자 정보 마스킹 처리 - 이미 보상 이름이 포함되어 있음
        return winnersWithReward.stream()
                .map(WinnerPublicInfo::of) // 새로 추가한 팩토리 메서드 사용
                .toList();
    }
}
