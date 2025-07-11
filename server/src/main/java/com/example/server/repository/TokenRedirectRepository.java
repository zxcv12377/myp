package com.example.server.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.server.entity.TokenRedirect;

public interface TokenRedirectRepository extends JpaRepository<TokenRedirect, Long> {

    Optional<TokenRedirect> findByShortId(String shortId);

    Integer deleteByCreatedAtBefore(LocalDateTime time);
}
