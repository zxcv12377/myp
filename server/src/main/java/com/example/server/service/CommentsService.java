package com.example.server.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.server.dto.CommentResponse;
import com.example.server.dto.CommentsDTO;
import com.example.server.entity.Board;
import com.example.server.entity.Comments;
import com.example.server.repository.BoardRepository;
import com.example.server.repository.CommentsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentsService {
    private final CommentsRepository commentRepository;
    private final BoardRepository boardRepository;

    public Comments create(CommentsDTO dto) {
        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
        Comments comment = Comments.builder()
                .content(dto.getContent())
                .likes(dto.getLikes())
                .hates(dto.getHates())
                .board(board)
                .build();

        if (dto.getParentId() != null) {
            Comments parent = commentRepository.findById((dto.getParentId()))
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글 없음"));
            comment.setParent(parent);
        }

        return commentRepository.save(comment);
    }

    public List<Comments> list() {
        return commentRepository.findAll();
    }

    public Comments getOne(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않거나 id가 일치하지 않습니다."));
    }

    public Comments update(Long id, CommentsDTO dto) {
        Comments comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        comment.changeContent(dto.getContent());
        return commentRepository.save(comment);
    }

    public void delete(Long id) {
        commentRepository.deleteById(id);
    }

    public List<CommentResponse> getCommentsByBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않음"));

        List<Comments> roots = commentRepository.findByBoardAndParentIsNull(board);

        return roots.stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
    }

}
