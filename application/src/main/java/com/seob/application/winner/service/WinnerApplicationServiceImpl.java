package com.seob.application.winner.service;

import com.seob.systeminfra.reward.exception.RewardNotFoundException;
import com.seob.systeminfra.entry.exception.UserNotFoundException;
import com.seob.application.winner.exception.AlreadyWinnerExistsException;
import com.seob.application.winner.exception.EntryNotFoundException;
import com.seob.application.winner.exception.NoRewardInEventException;
import com.seob.application.winner.exception.WinnerNotFoundException;
import com.seob.systemdomain.entry.repository.EntryRepository;
import com.seob.systemdomain.reward.repository.RewardRepository;
import com.seob.systemdomain.user.domain.vo.UserId;
import com.seob.systemdomain.user.repository.UserRepository;
import com.seob.systemdomain.winner.domain.WinnerDomain;
import com.seob.systemdomain.winner.dto.WinnerDetailInfo;
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
    private final UserRepository userRepository;
//    private final EmailService emailService;


    /**
     * 1) 이벤트 ID로 참가자들을 조회
     * 2) 이미 당첨자가 있으면 예외
     * 3) 무작위로 1명 추첨
     * 4) 보상(Reward) 식별자 조회 후
     * 5) 도메인 서비스로 WinnerDomain 생성
     */

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

    /**
     * 1) 당첨자(WinnerDomain) 조회
     * 2) 보상이 이미 발송된 상태면 return false
     * 3) 사용자 이메일, 보상 URL 조회
     * 4) 이메일 발송
     * 5) 도메인 상태(RewardStatus)를 COMPLETE로 변경
     *    (실패 시 FAILED 처리)
     */
    @Override
    public boolean sendReward(Long winnerId) {

        //당첨자 조회
        WinnerDomain winnerDomain = winnerService.findById(winnerId)
                .orElseThrow(() -> WinnerNotFoundException.EXCEPTION);

        //이미 발송된 상태인지 확인
        if(winnerDomain.getStatus() != RewardStatus.PENDING){
            return false;
        }

        try{
            String userId = winnerDomain.getUserId().getValue();
            String userEmail = userRepository.findById(winnerDomain.getUserId())
                    .orElseThrow(() -> UserNotFoundException.EXCEPTION)
                    .getEmail().getValue();

            String rewardUrl = rewardRepository.findById(winnerDomain.getRewardId())
                    .orElseThrow(() -> RewardNotFoundException.EXCEPTION)
                    .getResource_url();

            //이메일 발송 로직 추가


            winnerService.updateStatus(winnerDomain, RewardStatus.COMPLETE);

            return true;

        }catch(Exception e){
            winnerService.updateStatus(winnerDomain, RewardStatus.FAILED);
            return false;
        }


    }

    @Override
    @Transactional(readOnly = true)
    public List<WinnerUserDetailInfo> getWinnersByUserId(String userId) {
        return winnerQueryRepository.findUserDetailsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WinnerDetailInfo> getWinnersByEventId(Long eventId) {
        return winnerQueryRepository.findDetailsByEventId(eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WinnerDetailInfo> getAllWinners() {
        return winnerQueryRepository.findAllDetails();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WinnerDetailInfo> getWinnersByStatus(RewardStatus status) {
        return winnerQueryRepository.findDetailsByStatus(status);
    }
}
