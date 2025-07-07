package com.example.server.controller;

import java.util.List;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.server.dto.BoardDTO;
import com.example.server.entity.Board;
import com.example.server.repository.BoardRepository;
import com.example.server.service.BoardService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequestMapping("/board")
@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/")
    public ResponseEntity<List<Board>> list() {
        return ResponseEntity.ok(boardService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Board> getRow(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getOne(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Board> create(@RequestBody BoardDTO dto) {
        Board board = boardService.create(dto);
        return ResponseEntity.ok(board);
    }

    @PutMapping("/modify/{id}")
    public ResponseEntity<Board> update(@PathVariable Long id, @RequestBody BoardDTO dto) {
        Board board = boardService.update(id, dto);
        return ResponseEntity.ok(board);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRow(@PathVariable Long id) {
        boardService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/page")
    public ResponseEntity<List<Board>> getBoardPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(boardService.getBoardPage(page, size));
    }

}
