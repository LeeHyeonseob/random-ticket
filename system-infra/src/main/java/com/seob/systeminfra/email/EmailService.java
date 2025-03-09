package com.seob.systeminfra.email;

public interface EmailService {

    boolean sendRewardEmail(String to, String eventName, String rewardUrl);

    void sendVerificationEmail(String to, String verificationCode);
}
