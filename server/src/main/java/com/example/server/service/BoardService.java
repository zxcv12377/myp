package com.example.server.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.server.dto.BoardDTO;
import com.example.server.entity.Board;
import com.example.server.repository.BoardRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;

    public List<Board> list() {
        return boardRepository.findAll();
    }

    public Board getOne(Long id) {
        return boardRepository.findById(id).get();
    }

    public Board update(BoardDTO dto) {
        Board board = boardRepository.findById(dto.getId()).get();
        board.changeTitle(dto.getTitle());
        board.changeContent(dto.getContent());
        return board;
    }

    public Board create(BoardDTO dto) {
        Board board = Board.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();

        return boardRepository.save(board);
    }
}
