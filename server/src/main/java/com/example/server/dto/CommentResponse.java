package com.example.server.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.server.entity.Comments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private List<CommentResponse> children;
    private LocalDateTime createdDate;

    public CommentResponse(Comments comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.createdDate = comment.getCreatedDate();
        this.children = comment.getChildren() != null
                ? comment.getChildren().stream()
                        .map(CommentResponse::new)
                        .collect(Collectors.toList())
                : List.of();
    }
}
