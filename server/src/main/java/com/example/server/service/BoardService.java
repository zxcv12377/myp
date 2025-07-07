package com.example.server.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Board update(Long id, BoardDTO dto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 없습니다."));
        board.changeTitle(dto.getTitle());
        board.changeContent(dto.getContent());
        return boardRepository.save(board);
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    public Board create(BoardDTO dto) {
        Board board = Board.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();

        return boardRepository.save(board);
    }

    @Transactional(readOnly = true)
    public List<Board> getBoardPage(int page, int size) {
        return boardRepository.getBoardList(page, size);
    }
}
