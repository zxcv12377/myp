package com.example.server.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Setter
public class VerificationToken {
    @Id
    private String token;

    private String email;
    private String password;
    private String nickname;

    private LocalDateTime expiresAt;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
