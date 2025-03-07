package com.seob.systemcore.error.utils;

public class PrivacyUtils {

    public static String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) return email;

        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        String maskedLocalPart = localPart.charAt(0) +
                "*".repeat(Math.max(0, localPart.length() - 1));

        return maskedLocalPart + domain;
    }
}
