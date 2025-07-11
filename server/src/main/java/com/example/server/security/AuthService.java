package com.example.server.security;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.server.config.MailConfigCheck;
import com.example.server.dto.RegisterRequestDTO;
import com.example.server.entity.Member;
import com.example.server.entity.RefreshToken;
import com.example.server.entity.TokenRedirect;
import com.example.server.entity.VerificationToken;
import com.example.server.entity.enums.UserRole;
import com.example.server.exception.CustomException;
import com.example.server.repository.MemberRepository;
import com.example.server.repository.RefreshTokenRepository;
import com.example.server.repository.TokenRedirectRepository;
import com.example.server.repository.VerificationTokenRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthService {
    private final VerificationTokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final TokenRedirectRepository redirectRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final MailConfigCheck mailchk;
    private final DataSource dataSource;

    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtProvider;
    private final RefreshTokenRepository refreshRepository;

    @Value("${jwt.refresh-exp-ms}")
    private long refreshExpMs;

    public Map<String, String> login(String email, String pw) {
        // 1) 인증
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, pw));

        // 2) 토큰 발급
        String accessToken = jwtProvider.createAccessToken(auth);
        String refreshToken = jwtProvider.createRefreshToken(email);

        // 3) 리프레시 토큰 만료시간 계산
        Instant expInstant = Instant.now().plusMillis(refreshExpMs);
        LocalDateTime expiresAt = LocalDateTime.ofInstant(expInstant, ZoneId.systemDefault());

        // 4) DB에 저장
        RefreshToken rt = new RefreshToken();
        rt.setToken(refreshToken);
        rt.setEmail(email);
        rt.setExpiresAt(expiresAt);
        refreshRepository.save(rt);

        // 5) 클라이언트에 반환
        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken);
    }

    public void register(RegisterRequestDTO dto) {
        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("현재 이메일은 사용중 입니다.");
        }

        String token = UUID.randomUUID().toString();
        String shortId = UUID.randomUUID().toString().substring(0, 8);
        redirectRepository.save(new TokenRedirect(null, shortId, token, LocalDateTime.now()));
        VerificationToken vt = VerificationToken.builder()
                .token(token)
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .createdDate(dto.getCreatedDate())
                .updatedDate(dto.getUpdatedDate())
                .build();
        tokenRepository.save(vt);

        // 실 서비스 시에 주소를 바꿔주어야 함
        String link = "http://localhost:5173/email-verify/" + shortId;
        sendHtmlVerificationEmail(dto.getEmail(), link, token);
    }

    private void sendHtmlVerificationEmail(String to, String verifyUrl, String token) {
        mailchk.check();
        String subject = "이메일 인증 메세지";
        String text = "HTML 지원 메일에서 버튼을 눌러 인증을 완료하세요.";
        String content = "<p>안녕하세요!</p>"
                + "<p>아래 버튼을 눌러 인증을 완료해주세요.</p>"
                + "<a href=\"" + verifyUrl + "\" "
                + "style=\"display:inline-block;padding:12px 24px;"
                + "background-color:#1a73e8;color:#fff;"
                + "text-decoration:none;border-radius:4px;\">"
                + "이메일 인증하기"
                + "</a>";

        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, content);

            mailSender.send(msg);
        } catch (MailException | MessagingException e) {
            throw new IllegalStateException("이메일 전송 실패", e);
        }
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

    public String refresh(String refreshToken) {
        // 1) DB에 있나?
        RefreshToken rt = refreshRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CustomException("Invalid refresh token"));

        // 2) 만료 검사
        if (rt.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshRepository.delete(rt);
            throw new CustomException("Refresh token expired");
        }

        // 3) 새 액세스 토큰 발급
        return jwtProvider.createAccessToken(
                new UsernamePasswordAuthenticationToken(rt.getEmail(), null, List.of()));
    }

    @Scheduled(cron = "0 0 * * * *")
    public void purgeExpiredTokens() {
        int deleted = tokenRepository.deleteExpired(LocalDateTime.now());
        log.info("만료된 토큰 삭제: {}", deleted);
    }

    @Scheduled(cron = "0 0 * * * *")
    public void purgeExpiredRedirects() {
        int deleted = redirectRepository.deleteByCreatedAtBefore(LocalDateTime.now().minusMinutes(5));
        log.info("만료된 redirect 삭제: {}", deleted);
    }

    public void logout(String refreshToken) {
        // 리프레시 토큰을 제거하면 더 이상 재발급 불가
        refreshRepository.deleteByToken(refreshToken);
    }
}