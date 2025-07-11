package com.example.server.security;

import java.util.Collections;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.server.entity.Member;
import com.example.server.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("UserDetailsService: 이메일로 멤버 조회 중: {}", email);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 찾을 수 없습니다 : " + email));

        log.debug("UserDetailsService: 조회된 멤버 ID: {}", member.getId()); // <-- 중요: 이 로그를 확인
        log.debug("UserDetailsService: 조회된 멤버 이메일 인증 여부: {}", member.getEmailverified());

        if (!Boolean.TRUE.equals(member.getEmailverified())) {
            throw new DisabledException("인증되지 않은 이메일입니다.");
        }

        return new CustomUserDetails(
                member.getId(),
                member.getEmail(),
                member.getPassword(),
                member.getEmailverified(),
                member.getRoles() != null ? member.getRoles() : Collections.emptySet());
    }

}
