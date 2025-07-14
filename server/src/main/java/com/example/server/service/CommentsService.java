package com.example.server.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.server.dto.CommentsDTO;
import com.example.server.entity.Board;
import com.example.server.entity.Comments;
import com.example.server.entity.Member;
import com.example.server.mapper.CommentMapper;
import com.example.server.repository.BoardRepository;
import com.example.server.repository.CommentsRepository;
import com.example.server.repository.MemberRepository;
import com.example.server.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class CommentsService {
    private final CommentsRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final CommentMapper commentMapper;

    public CommentsDTO create(CommentsDTO dto, CustomUserDetails user) {
        Member member = memberRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("로그인 하지않음"));
        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
        Comments comment = Comments.builder()
                .content(dto.getContent())
                .likes(dto.getLikes())
                .hates(dto.getHates())
                .board(board)
                .member(member)
                .build();

        log.info("이것은 코멘토" + comment.getMember().getId());

        if (dto.getParentId() != null) {
            Comments parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글 없음"));
            comment.setParent(parent);
        }
        Comments saved = commentRepository.save(comment);
        return commentMapper.toDto(saved);
    }

    public List<CommentsDTO> list() {
        List<Comments> list = commentRepository.findAll();
        return commentMapper.toDtoList(list);
    }

    public CommentsDTO getOne(Long id) {
        Comments comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다."));
        return commentMapper.toDto(comment);
    }

    public CommentsDTO update(Long id, CommentsDTO dto) {
        Comments comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다."));
        comment.changeContent(dto.getContent());
        Comments saved = commentRepository.save(comment);
        return commentMapper.toDto(saved);
    }

    public void delete(Long id) {
        commentRepository.deleteById(id);
    }

    public List<CommentsDTO> getCommentsByBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글이 없습니다."));
        List<Comments> roots = commentRepository.findByBoardAndParentIsNull(board);
        return commentMapper.toDtoList(roots);
    }

}
