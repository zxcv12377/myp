package com.example.server.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Embeddable
public class ViewLogId implements Serializable {
    @Column(name = "member_id")
    private Long memberId;
    @Column(name = "board_id")
    private Long boardId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ViewLogId))
            return false;
        ViewLogId that = (ViewLogId) o;
        return Objects.equals(memberId, that.memberId)
                && Objects.equals(boardId, that.boardId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, boardId);
    }
}
