package com.example.server.dto;

import java.time.LocalDate;

import com.example.server.entity.Board;

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
    private String content;

    private LocalDate createdDate;

    private Long views;
    private Long likes;

    public BoardDTO entityToDto(Board board) {
        BoardDTO dto = BoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .build();
        return dto;
    }

    public Board dtoToEntity(BoardDTO dto) {
        Board board = Board.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();
        return board;
    }
}
