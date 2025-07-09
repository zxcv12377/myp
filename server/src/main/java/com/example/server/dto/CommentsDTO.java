package com.example.server.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter

public class CommentsDTO {
    private Long id;
    private String content;
    private Long likes;
    private Long hates;

    private Long boardId;

    private LocalDateTime createdDate;

    // 대댓글
    private Long parentId;
    private List<CommentsDTO> children;
}
