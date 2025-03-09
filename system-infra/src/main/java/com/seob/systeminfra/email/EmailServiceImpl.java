package com.seob.systeminfra.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String fromEmail = "noreply@random-ticket.com";

    @Override
    public boolean sendRewardEmail(String to, String eventName, String rewardUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("랜덤 티켓 당첨!");

            // 템플릿에 전달할 변수 설정
            Context context = new Context();
            context.setVariable("eventName", eventName);
            context.setVariable("rewardUrl", rewardUrl);
            context.setVariable("currentYear", java.time.Year.now().getValue());

            // HTML 템플릿 처리
            String htmlContent = templateEngine.process("reward-email-template", context);
            helper.setText(htmlContent, true);

            // 이메일 발송
            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }

    @Override
    public void sendVerificationEmail(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("이메일 주소 인증 코드");
        message.setText("다음 인증코드를 입력하여 회원가입을 완료하세요!:" + verificationCode);

        mailSender.send(message);
    }


}
