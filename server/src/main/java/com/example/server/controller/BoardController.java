package com.example.server.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.server.dto.BoardDTO;
import com.example.server.dto.BoardResponseDTO;
import com.example.server.entity.Board;
import com.example.server.entity.Member;
import com.example.server.security.CustomUserDetails;
import com.example.server.service.BoardService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Log4j2
@RequestMapping("/board")
@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/")
    public ResponseEntity<List<BoardResponseDTO>> list(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long memberId = customUserDetails.getId();
        return ResponseEntity.ok(boardService.list(memberId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDTO> getRow(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long memberId = customUserDetails.getId();
        return ResponseEntity.ok(boardService.getOne(id, memberId));
    }

    @GetMapping("/page")
    public ResponseEntity<List<BoardResponseDTO>> getBoardPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long memberId = customUserDetails.getId();
        return ResponseEntity.ok(boardService.getBoardPage(page, size, memberId));
    }

    @GetMapping("/top5")
    public ResponseEntity<List<BoardResponseDTO>> getTop5(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long memberId = customUserDetails.getId();
        return ResponseEntity.ok(boardService.getTob5Boards(memberId));
    }

    @PostMapping("/create")
    public ResponseEntity<BoardResponseDTO> create(@RequestBody @Valid BoardDTO dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long memberId = customUserDetails.getId();
        if (memberId == null) {
            log.error("게시글 생성 시 로그인된 사용자 ID를 찾을 수 없습니다.");
            throw new IllegalArgumentException("게시글 작성을 위해서는 로그인이 필요합니다.");
        }
        BoardResponseDTO boardResponseDTO = boardService.create(dto, memberId);
        return ResponseEntity.ok(boardResponseDTO);
    }

    @PutMapping("/modify/{id}")
    public ResponseEntity<BoardDTO> update(@PathVariable Long id, @RequestBody @Valid BoardDTO dto) {
        BoardDTO boardDTO = boardService.update(id, dto);
        return ResponseEntity.ok(boardDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRow(@PathVariable Long id) {
        boardService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<BoardResponseDTO> getBoardDetail(@PathVariable("id") Long boardId,
            HttpServletRequest request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long currentMemberId = customUserDetails.getId();
        log.info("디테일유저 ID: " + currentMemberId);

        BoardResponseDTO boardResponseDTO = boardService.getBoardDetail(boardId, currentMemberId);
        return ResponseEntity.ok(boardResponseDTO);
    }

    @PutMapping("/{boardId}/likes") // RESTful하게 URL 변경
    public ResponseEntity<Long> toggleBoardLike(@PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Long loggedInMemberId = null;

        if (customUserDetails != null) {
            loggedInMemberId = customUserDetails.getId(); //
        }
        log.info("로그인된 사용자 ID: " + loggedInMemberId);

        if (loggedInMemberId == null) {
            throw new IllegalArgumentException("로그인된 사용자 ID를 찾을 수 없습니다.");
        }
        long updatedLikesCount = boardService.toggleLike(boardId, loggedInMemberId);
        // 업데이트된 좋아요 수만 클라이언트에게 반환
        return ResponseEntity.ok(updatedLikesCount);
    }

}
