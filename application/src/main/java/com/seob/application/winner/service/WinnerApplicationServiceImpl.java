package com.seob.application.winner.service;

import com.seob.application.common.utils.SecurityUtils;
import com.seob.systemdomain.event.repository.EventRepository;
import com.seob.systemdomain.winner.dto.WinnerPublicInfo;
import com.seob.systemdomain.winner.dto.WinnerRewardDetailInfo;
import com.seob.systeminfra.email.EmailService;
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
    private final EventRepository eventRepository;
    private final EmailService emailService;


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
                    .getResourceUrl();

            String eventName = eventRepository.findById(winnerDomain.getEventId()).getName();

            //이메일 발송 로직 추가
            emailService.sendRewardEmail(userEmail, eventName, rewardUrl);


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
        // 사용자 레포지토리에서 이름과 이메일로 사용자 찾기
        List<UserId> userIds = userRepository.findByNameAndEmail(name, email);
        
        if (userIds.isEmpty()) {
            return false;
        }
        
        // 찾은 사용자들 중에 당첨자가 있는지 확인
        for (UserId userId : userIds) {
            List<WinnerUserDetailInfo> winners = winnerQueryRepository.findUserDetailsByUserId(userId.getValue());
            if (!winners.isEmpty()) {
                return true;
            }
        }
        
        return false;
    }
    
    //현재 로그인한 사용자의 당첨 내역 조회
    @Override
    @Transactional(readOnly = true)
    public List<WinnerUserDetailInfo> getMyWinners() {
        // 현재 로그인한 사용자 ID 가져오기
        String currentUserId = SecurityUtils.getCurrentUserId();
        return winnerQueryRepository.findUserDetailsByUserId(currentUserId);
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
