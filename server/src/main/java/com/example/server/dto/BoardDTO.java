package com.example.server.dto;

import java.time.LocalDateTime;

import com.example.server.entity.Board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDTO {
    private Long id;
    @NotBlank(message = "댓글 내용은 공백일 수 없습니다.")
    @Size(max = 20, message = "제목은 20글자 까지만 가능합니다.")
    private String title;
    @NotBlank(message = "댓글 내용은 공백일 수 없습니다.")
    private String content;

    private Long memberId;

    private String imagePath;

    private LocalDateTime createdDate;

}
