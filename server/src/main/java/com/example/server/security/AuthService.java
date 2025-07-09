package com.example.server.security;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.server.config.MailConfigCheck;
import com.example.server.dto.RegisterRequestDTO;
import com.example.server.entity.Member;
import com.example.server.entity.VerificationToken;
import com.example.server.entity.enums.UserRole;
import com.example.server.exception.CustomException;
import com.example.server.repository.MemberRepository;
import com.example.server.repository.VerificationTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthService {
    private final VerificationTokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final MailConfigCheck mailchk;
    private final DataSource dataSource;

    // public void register(RegisterRequestDTO dto) {
    // if (memberRepository.existsByEmail(dto.getEmail())) {
    // throw new IllegalArgumentException("현재 이메일은 사용중 입니다.");
    // }

    // String token = UUID.randomUUID().toString();
    // VerificationToken vt = VerificationToken.builder()
    // .token(token)
    // .email(dto.getEmail())
    // .password(passwordEncoder.encode(dto.getPassword()))
    // .nickname(dto.getNickname())
    // .expiresAt(LocalDateTime.now().plusMinutes(5))
    // .createdDate(dto.getCreatedDate())
    // .updatedDate(dto.getUpdatedDate())
    // .build();
    // tokenRepository.save(vt);

    // // 실 서비스 시에 주소를 바꿔주어야 함
    // String link = "http://localhost:5173/email-verify?token=" + token;
    // SimpleMailMessage mail = new SimpleMailMessage();
    // mail.setTo(dto.getEmail());
    // mail.setSubject("이메일 인증 메세지");
    // mail.setText("이 링크를 눌러서 인증해 주세요 :\n" + link);
    // mailchk.check();
    // try {
    // mailSender.send(mail);
    // } catch (MailException e) {
    // e.printStackTrace();
    // throw new IllegalStateException("이메일 전송 실패", e);
    // }
    // }

    public String register(RegisterRequestDTO dto) {
        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("현재 이메일은 사용중 입니다.");
        }
        String token = UUID.randomUUID().toString();
        VerificationToken vt = VerificationToken.builder()
                .token(token)
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
        tokenRepository.save(vt);

        String link = "http://localhost:5173/email-verify?token=" + token;
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(dto.getEmail());
        mail.setSubject("이메일 인증 메세지");
        mail.setText("링크를 눌러 인증해 주세요:\n" + link);
        mailchk.check();
        try {
            mailSender.send(mail);
        } catch (MailException e) {
            log.error("이메일 전송 실패", e);
            throw new IllegalStateException("이메일 전송 실패", e);
        }
        return token;
    }

    @Transactional
    public String verify(String token) {
        VerificationToken vt = tokenRepository.findById(token).orElseThrow(() -> new CustomException("유효하지 않은 토큰입니다."));
        if (vt.getExpiresAt().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(vt);
            throw new CustomException("토큰이 만료되었습니다.");
        }

        String result;
        try (Connection conn = dataSource.getConnection();
                CallableStatement cs = conn.prepareCall("{call TEST.auth_pkg.VERIFY_EMAIL_BY_TOKEN(?, ?)}")) {
            cs.setString(1, token);
            cs.registerOutParameter(2, Types.VARCHAR);
            cs.execute();
            result = cs.getString(2);
        } catch (SQLException e) {
            log.error("프로시저 호출 중 오류", e);
            throw new CustomException("인증 처리 중 DB 오류가 발생했습니다.", e);
        }
        log.info("리조트 : {}", result);
        if (!"SUCCESS".equalsIgnoreCase(result)) {
            throw new CustomException("인증 실패: " + result);
        }
        return "SUCCESS";
    }

    @Scheduled(cron = "0 0 * * * *")
    public void purgeExpiredTokens() {
        int deleted = tokenRepository.deleteExpired(LocalDateTime.now());
        log.info("만료된 토큰 삭제: {}", deleted);
    }
}