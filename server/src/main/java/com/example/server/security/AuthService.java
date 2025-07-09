package com.example.server.security;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.server.dto.RegisterRequestDTO;
import com.example.server.entity.Member;
import com.example.server.entity.enums.UserRole;
import com.example.server.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequestDTO dto) {
        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("현재 이메일은 사용중 입니다.");
        }
        Member m = Member.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .roles(Set.of(UserRole.USER))
                .emailverified(false)
                .verificationToken(UUID.randomUUID().toString())
                .tokenExpiry(LocalDateTime.now().plusMinutes(5))
                .build();
        memberRepository.save(m);

        String link = "https://your-domain.com/auth/verify?token=" + m.getVerificationToken();
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(m.getEmail());
        mail.setSubject("이메일 인증 메세지");
        mail.setText("이 링크를 눌러서 인증해 주세요 :\n" + link);
        mailSender.send(mail);
    }

    public void verify(String token) {
        Member member = memberRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다"));
        if (member.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("토큰 시간 만료");
        }
        member.changeEmailverified(true);
        member.changeVerificationToken(null);
        member.changeTokenExpiry(null);
        memberRepository.save(member);
    }

}