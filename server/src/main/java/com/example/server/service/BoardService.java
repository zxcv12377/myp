package com.example.server.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.server.dto.BoardDTO;
import com.example.server.dto.BoardResponseDTO;
import com.example.server.entity.Board;
import com.example.server.entity.Like;
import com.example.server.entity.Member;
import com.example.server.entity.ViewLog;
import com.example.server.entity.ViewLogId;
import com.example.server.mapper.BoardMapper;
import com.example.server.repository.BoardRepository;
import com.example.server.repository.LikeRepository;
import com.example.server.repository.MemberRepository;
import com.example.server.repository.ViewLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;
    private final ViewLogRepository viewLogRepository;
    private final BoardMapper bMapper;

    @Transactional(readOnly = true)
    public List<BoardResponseDTO> list(Long currentMemberId) { // currentMemberId 추가
        List<Board> boards = boardRepository.findAll();
        // 각 Board 엔티티를 BoardResponseDTO로 변환하면서 좋아요 여부 계산
        return boards.stream()
                .map(board -> {
                    boolean likedByUser = false;
                    if (currentMemberId != null) {
                        Member currentMember = memberRepository.findById(currentMemberId).orElse(null);
                        if (currentMember != null) {
                            likedByUser = likeRepository.findByBoardAndMember(board, currentMember).isPresent();
                        }
                    }
                    return bMapper.toDtoWithLikedStatus(board, likedByUser); // ⭐ Mapper 사용
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BoardResponseDTO getOne(Long id, Long currentMemberId) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + id));
        boolean likedByUser = false;
        if (currentMemberId != null) {
            Member currentMember = memberRepository.findById(currentMemberId).orElse(null);
            if (currentMember != null) {
                likedByUser = likeRepository.findByBoardAndMember(board, currentMember).isPresent();
            }
        }
        return bMapper.toDtoWithLikedStatus(board, likedByUser);
    }

    @Transactional
    public BoardDTO update(Long id, BoardDTO dto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 없습니다."));
        board.changeTitle(dto.getTitle());
        board.changeContent(dto.getContent());
        Board updatedBoard = boardRepository.save(board);
        return bMapper.toDto(updatedBoard);
    }

    @Transactional
    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    @Transactional
    public BoardResponseDTO create(BoardDTO dto, Long id) {

        Member author = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작성자(Member)를 찾을 수 없습니다. ID: " + id));
        Board board = bMapper.toEntity(dto);
        board.setMember(author);

        Board savedBoard = boardRepository.save(board);

        return bMapper.toDtoWithLikedStatus(savedBoard, false);
    }

    @Transactional(readOnly = true)
    public List<BoardResponseDTO> getBoardPage(int page, int size, Long currentMemberId) {
        List<Board> boards = boardRepository.getBoardList(page, size);
        return boards.stream().map(board -> {
            boolean likedByUser = false;
            if (currentMemberId != null) {
                Member currentMember = memberRepository.findById(currentMemberId).orElse(null);
                if (currentMember != null) {
                    likedByUser = likeRepository.findByBoardAndMember(board, currentMember).isPresent();
                }
            }
            return bMapper.toDtoWithLikedStatus(board, likedByUser);
        }).collect(Collectors.toList());
    }

    @Transactional
    public List<BoardResponseDTO> getTob5Boards(Long currentMemberId) {
        List<Board> top5Boards = boardRepository.findTop5ByOrderByCreatedDateDesc();
        return top5Boards.stream()
                .map(board -> {
                    boolean likedByUser = false;
                    if (currentMemberId != null) {
                        Member currentMember = memberRepository.findById(currentMemberId).orElse(null);
                        if (currentMember != null) {
                            likedByUser = likeRepository.findByBoardAndMember(board, currentMember).isPresent();
                        }
                    }
                    return bMapper.toDtoWithLikedStatus(board, likedByUser); // ⭐ Mapper 사용
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BoardResponseDTO> getLikeTop5Boards(Long currentMemberId) {
        List<Board> top5Boards = boardRepository.getTop5ByLikes();
        return top5Boards.stream()
                .map(board -> {
                    boolean likedByUser = false;
                    if (currentMemberId != null) {
                        Member currentMember = memberRepository.findById(currentMemberId).orElse(null);
                        if (currentMember != null) {
                            likedByUser = likeRepository.findByBoardAndMember(board, currentMember).isPresent();
                        }
                    }
                    return bMapper.toDtoWithLikedStatus(board, likedByUser); // ⭐ Mapper 사용
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true) // 읽기 전용 트랜잭션 추가 (BoardResponseDTO 반환 시)
    public BoardResponseDTO getBoardDetail(Long boardId, Long currentMemberId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + boardId));

        boolean likedByUser = false;
        if (currentMemberId != null) {
            Member currentMember = memberRepository.findById(currentMemberId).orElse(null);
            if (currentMember != null) {
                likedByUser = likeRepository.findByBoardAndMember(board, currentMember).isPresent();
            }
        }
        return bMapper.toDtoWithLikedStatus(board, likedByUser);
    }

    @Transactional
    public long toggleLike(Long boardId, Long memberId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        Like existingLike = likeRepository.findByBoardAndMember(board, member).orElse(null);

        if (existingLike != null) {
            likeRepository.delete(existingLike);
            log.info("게시글 {} 좋아요 취소. 사용자: {}", boardId, memberId);
        } else {
            Like newLike = Like.builder()
                    .board(board)
                    .member(member)
                    .build();
            likeRepository.save(newLike);
            log.info("게시글 {} 좋아요 추가. 사용자: {}", boardId, memberId);
        }

        long updatedLikesCount = likeRepository.countByBoard(board);
        return updatedLikesCount;
    }

    @Transactional
    public void increaseViewCount(Long boardId) {
        boardRepository.incrementViewCount(boardId);
    }

    @Transactional
    public void countViewForMember(Long memberId, Long boardId) {
        ViewLogId key = new ViewLogId(memberId, boardId);
        if (!viewLogRepository.existsById(key)) {
            viewLogRepository.save(new ViewLog(key));
            boardRepository.incrementViewCount(boardId);
        }
    }
}
