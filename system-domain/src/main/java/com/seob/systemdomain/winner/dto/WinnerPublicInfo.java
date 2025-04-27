package com.seob.systemdomain.winner.dto;

import java.time.LocalDateTime;

//당첨자 공개 정보 (마스킹 처리)
public record WinnerPublicInfo(
    Long eventId,
    String eventName,
    String maskedName,  // 마스킹 처리된 사용자 이름
    String maskedEmail, // 마스킹 처리된 이메일
    String eventDescription, // 보상 이름 대신 이벤트 설명 사용
    LocalDateTime announcedAt
) {
    // WinnerRewardDetailInfo를 사용하는 팩토리 메서드
    public static WinnerPublicInfo of(WinnerRewardDetailInfo detailInfo) {
        return new WinnerPublicInfo(
            detailInfo.eventId(),
            detailInfo.eventName(),
            maskName(detailInfo.nickName()),
            maskEmail(detailInfo.userEmail()),
            detailInfo.eventDescription(), // 이벤트 설명 사용
            detailInfo.sentAt()
        );
    }
    
    //이름 마스킹 로직
    private static String maskName(String name) {
        if (name == null || name.isEmpty()) {
            return "***";
        }
        
        if (name.length() <= 2) {
            return name.charAt(0) + "*";
        } else {
            // 가운데 글자만 마스킹
            return name.charAt(0) + "*".repeat(name.length() - 2) + name.charAt(name.length() - 1);
        }
    }
    
    //이메일 마스킹 로직
    private static String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "***@***.com";
        }
        
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return email; // 이메일 형식이 아닌 경우 원본 반환
        }
        
        String username = parts[0];
        String domain = parts[1];
        
        // 아이디 마스킹
        String maskedUsername;
        if (username.length() <= 3) {
            maskedUsername = username.charAt(0) + "*".repeat(username.length() - 1);
        } else {
            maskedUsername = username.substring(0, 2) + 
                           "*".repeat(username.length() - 3) + 
                           username.charAt(username.length() - 1);
        }
        
        // 도메인 마스킹
        String[] domainParts = domain.split("\\.");
        String maskedDomain;
        if (domainParts.length >= 2) {
            // 최상위 도메인(.com, .net 등)은 유지
            String tld = domainParts[domainParts.length - 1];
            // 도메인 이름의 첫 두 글자만 보이고 나머지는 마스킹
            String domainName = domainParts[0];
            String maskedDomainName;
            
            if (domainName.length() <= 3) {
                maskedDomainName = domainName.charAt(0) + "*".repeat(domainName.length() - 1);
            } else {
                maskedDomainName = domainName.substring(0, 2) + "*".repeat(domainName.length() - 2);
            }
            
            maskedDomain = maskedDomainName + "." + tld;
        } else {
            maskedDomain = domain;
        }
        
        return maskedUsername + "@" + maskedDomain;
    }
}
