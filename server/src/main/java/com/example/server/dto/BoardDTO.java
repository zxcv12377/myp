package com.example.server.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDTO {
    private Long id;
    private String title;
    @NotBlank(message = "댓글 내용은 공백일 수 없습니다.")
    private String content;

    private LocalDateTime createdDate;

    private Long views;
    private Long likes;

}
