package com.example.server.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.server.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, String> {
    Optional<VerificationToken> findByToken(String token);

    @Modifying
    @Query("DELETE FROM VerificationToken t WHERE t.expiresAt < :now")
    int deleteExpired(@Param("now") LocalDateTime now);
}