package com.example.server.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ViewLog {
    @EmbeddedId
    private ViewLogId id;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime viewedAt = LocalDateTime.now();

    public ViewLog(ViewLogId id) {
        this.id = id;
        this.viewedAt = LocalDateTime.now();
    }
}
