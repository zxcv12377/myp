package com.example.server.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.server.Base.BaseEntity;
import com.example.server.entity.enums.UserRole;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String nickname;
    private String password;
    private String profile;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "member_roles", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "role")
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles = new HashSet<>();

    @Column(nullable = false)
    private boolean emailverified;

    @OneToMany(mappedBy = "member")
    private List<Board> boards;

    @OneToMany(mappedBy = "member")
    private List<Comments> comments;

    public void changeEmailverified(boolean emailverified) {
        this.emailverified = emailverified;
    }

    @PrePersist
    private void ensureDefaultRole() {
        if (roles.isEmpty()) {
            roles.add(UserRole.USER);
        }
    }
}
