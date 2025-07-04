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

    @PutMapping("path/{id}")
    public String putMethodName(@PathVariable String id, @RequestBody String entity) {
        // TODO: process PUT request

        return entity;
    }

    @DeleteMapping("/delete/{id}")
    public void deleteRow(Long id) {
        boardService.delete(id);
    }

}
