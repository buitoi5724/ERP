package com.example.erp.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetToken(String toEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Đặt lại mật khẩu - Hệ thống ERP");
        message.setText(
            "Xin chào,\n\n" +
            "Mã đặt lại mật khẩu của bạn là:\n" +
            token + "\n\n" +
            "Mã này có hiệu lực trong 1 giờ.\n" +
            "Trân trọng!"
        );
        mailSender.send(message);
    }
}