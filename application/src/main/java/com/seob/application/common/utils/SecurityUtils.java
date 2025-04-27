package com.seob.application.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

//시큐리티 관련 유틸리티 클래스
public class SecurityUtils {

    //현재 인증된 사용자의 ID 가져오기
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("현재 인증된 사용자가 없습니다.");
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        
        throw new IllegalStateException("인증된 사용자 정보를 가져올 수 없습니다.");
    }
    
    //현재 인증된 사용자의 ID를 가져오되, 없을 경우 null 반환
    public static String getCurrentUserIdOrNull() {
        try {
            return getCurrentUserId();
        } catch (Exception e) {
            return null;
        }
    }
    
    //현재 인증된 사용자가 특정 권한을 가지고 있는지 확인
    public static boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }
    
    //현재 사용자가 관리자인지 확인
    public static boolean isAdmin() {
        return hasAuthority("ROLE_ADMIN");
    }
    
    private SecurityUtils() {
        // 유틸리티 클래스는 인스턴스화 방지
    }
}
