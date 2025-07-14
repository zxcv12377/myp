package com.example.server.dto;

import java.time.LocalDateTime;

import com.example.server.entity.Board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResponseDTO {
    private Long id;
    private String title;
    private String content;
    private Long likesCount;
    private LocalDateTime createdDate;
    private String nickname;
    private boolean likedByUser; // 좋아요 여부
    private Long memberId;
    private Long viewCount;

    public void setLikedByUser(boolean likedByUser) {
        this.likedByUser = likedByUser;
    }

    public static BoardResponseDTO fromEntity(Board board, boolean likedByUser) {
        String nickname = null;
        if (board.getMember() != null) { // ⭐ 이 널 체크가 중요합니다.
            nickname = board.getMember().getNickname();
        }
        return BoardResponseDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .likesCount(board.getLikesCount()) // Board 엔티티의 메서드 호출
                .createdDate(board.getCreatedDate())
                .nickname(nickname)
                .likedByUser(likedByUser) // 전달받은 likedByUser 값 설정
                .viewCount(board.getViewCount())
                .build();
    }
}
