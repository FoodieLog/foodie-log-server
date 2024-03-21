package com.foodielog.server._core.smtp;

import com.foodielog.server._core.error.exception.Exception500;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {
    private final JavaMailSender emailSender;

    public String createVerificationCode() {
        try {
            Random random = SecureRandom.getInstanceStrong();
            int randomNumber = random.nextInt(9000) + 1000;

            return Integer.toString(randomNumber);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception500("서버 에러: 이메일 인증 코드 생성 오류");
        }
    }

    public void sendEmail(String toEmail, String title, String text) {
        SimpleMailMessage emailForm = createEmailForm(toEmail, title, text);
        try {
            emailSender.send(emailForm);
        } catch (RuntimeException e) {
            log.debug("MailService.sendEmail exception occur toEmail: {}, " +
                    "title: {}, text: {}", toEmail, title, text);
            throw new Exception500("서버 에러 #E2");
        }
    }

    // 발신할 이메일 데이터 세팅
    private SimpleMailMessage createEmailForm(String toEmail, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);

        return message;
    }
}


