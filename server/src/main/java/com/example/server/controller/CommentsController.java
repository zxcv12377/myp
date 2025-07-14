package com.example.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.server.dto.CommentsDTO;
import com.example.server.entity.Comments;
import com.example.server.security.CustomUserDetails;
import com.example.server.service.CommentsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentsController {
    private final CommentsService commentService;

    @PostMapping("/create")
    public ResponseEntity<CommentsDTO> create(@RequestBody @Valid CommentsDTO dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(commentService.create(dto, customUserDetails));
    }

    @GetMapping("/")
    public ResponseEntity<List<CommentsDTO>> getList(@RequestParam CommentsDTO dto) {
        return ResponseEntity.ok(commentService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentsDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getOne(id));
    }

    @PutMapping("modify/{id}")
    public ResponseEntity<CommentsDTO> modify(@PathVariable Long id, @RequestBody CommentsDTO dto) {
        return ResponseEntity.ok(commentService.update(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<CommentsDTO>> getCommentsByBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(commentService.getCommentsByBoard(boardId));
    }

}
