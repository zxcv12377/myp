package com.example.server.dto;

import java.time.LocalDateTime;

import com.example.server.entity.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Long id;
    private String email;
    private String nickname;
    private String password;
    private String profile;
    private UserRole userRole;

    private boolean emailverified;
    private String verificationToken;
    private LocalDateTime tokenExpiry;
}
