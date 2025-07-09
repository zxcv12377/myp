package com.example.server.config;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MailConfigCheck {
    private final JavaMailSender mailSender;

    @PostConstruct
    public void check() {
        JavaMailSenderImpl impl = (JavaMailSenderImpl) mailSender;
        System.out.println("✅ Host: " + impl.getHost());
        System.out.println("✅ Port: " + impl.getPort());
        System.out.println("✅ Username: " + impl.getUsername());
    }
}