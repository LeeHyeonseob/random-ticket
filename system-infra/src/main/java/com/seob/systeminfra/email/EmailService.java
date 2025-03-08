package com.seob.systeminfra.email;

public interface EmailService {

    boolean sendRewardEmail(String to, String subject, String eventName, String rewardUrl);
}
