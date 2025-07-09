package com.example.server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.server.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByVerificationToken(String verificationToken);

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);
}
