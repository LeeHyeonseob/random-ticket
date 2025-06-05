package com.seob.systeminfra.email;

import com.seob.systeminfra.exception.EmailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
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
            log.info("보상 이메일 발송 성공: to={}, eventName={}", to, eventName);
            return true;
        } catch (MessagingException e) {
            log.error("보상 이메일 발송 실패 (메시지 생성 오류): to={}, eventName={}, error={}", to, eventName, e.getMessage(), e);
            return false;
        } catch (MailException e) {
            log.error("보상 이메일 발송 실패 (전송 오류): to={}, eventName={}, error={}", to, eventName, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("보상 이메일 발송 실패 (예상치 못한 오류): to={}, eventName={}, error={}", to, eventName, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void sendVerificationEmail(String to, String verificationCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("이메일 주소 인증 코드");
            message.setText("다음 인증코드를 입력하여 회원가입을 완료하세요!:" + verificationCode);

            mailSender.send(message);
            log.info("인증 이메일 발송 성공: to={}", to);
        } catch (MailException e) {
            log.error("인증 이메일 발송 실패: to={}, error={}", to, e.getMessage(), e);
            throw EmailSendException.SEND_FAILED;
        } catch (Exception e) {
            log.error("인증 이메일 발송 실패 (예상치 못한 오류): to={}, error={}", to, e.getMessage(), e);
            throw EmailSendException.SEND_FAILED;
        }
    }
}
