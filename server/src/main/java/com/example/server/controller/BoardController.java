package com.example.server.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.server.dto.BoardDTO;
import com.example.server.dto.BoardResponseDTO;
import com.example.server.entity.Board;
import com.example.server.entity.Member;
import com.example.server.security.CustomUserDetails;
import com.example.server.service.BoardService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
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
        Long memberId = (customUserDetails != null)
                ? customUserDetails.getId()
                : null;
        return ResponseEntity.ok(boardService.list(memberId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDTO> getRow(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            HttpServletRequest req,
            HttpServletResponse res) {
        Long memberId = (customUserDetails != null)
                ? customUserDetails.getId()
                : null;
        if (memberId != null) {
            // 로그인 유저: DB 기록 기반
            boardService.countViewForMember(memberId, id);
        } else {
            // 비회원: 브라우저 쿠키 기반
            String cookieName = "viewed_" + id;
            boolean viewed = Arrays.stream(Optional.ofNullable(req.getCookies()).orElse(new Cookie[0]))
                    .anyMatch(c -> c.getName().equals(cookieName));
            if (!viewed) {
                boardService.increaseViewCount(id);
                Cookie c = new Cookie(cookieName, "true");
                c.setMaxAge(24 * 60 * 60); // 1일
                c.setPath("/"); // 전체 경로
                res.addCookie(c);
            }
        }
        BoardResponseDTO dto = boardService.getBoardDetail(id, memberId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/page")
    public ResponseEntity<List<BoardResponseDTO>> getBoardPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long memberId = (customUserDetails != null)
                ? customUserDetails.getId()
                : null;
        return ResponseEntity.ok(boardService.getBoardPage(page, size, memberId));
    }

    @GetMapping("/top5/new")
    public ResponseEntity<List<BoardResponseDTO>> getTop5New(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long memberId = (customUserDetails != null)
                ? customUserDetails.getId()
                : null;
        return ResponseEntity.ok(boardService.getTob5Boards(memberId));
    }

    @GetMapping("/top5/like")
    public ResponseEntity<List<BoardResponseDTO>> getTop5Like(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long memberId = (customUserDetails != null)
                ? customUserDetails.getId()
                : null;
        return ResponseEntity.ok(boardService.getLikeTop5Boards(memberId));
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BoardResponseDTO> create(
            @ModelAttribute @Valid BoardDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long memberId = customUserDetails.getId();
        if (memberId == null) {
            log.error("게시글 생성 시 로그인된 사용자 ID를 찾을 수 없습니다.");
            throw new IllegalArgumentException("게시글 작성을 위해서는 로그인이 필요합니다.");
        }
        BoardResponseDTO boardResponseDTO = boardService.create(dto, memberId, image);
        return ResponseEntity.ok(boardResponseDTO);
    }

    @PutMapping(value = "/modify/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BoardDTO> update(@PathVariable Long id, @ModelAttribute @Valid BoardDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long memberId = (customUserDetails != null)
                ? customUserDetails.getId()
                : null;
        BoardDTO boardDTO = boardService.update(id, dto, image, memberId);
        return ResponseEntity.ok(boardDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRow(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long memberId = (customUserDetails != null)
                ? customUserDetails.getId()
                : null;
        boardService.delete(id, memberId);
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

    @GetMapping("/view/{id}")
    public ResponseEntity<BoardResponseDTO> viewBoard(
            @PathVariable Long id,
            @RequestParam(required = false) Long currentMemberId,
            HttpSession session) {
        // 세션에 “viewed_{id}” 키 없을 때만 조회수 증가

        // DTO 반환 (좋아요 여부 등 포함)
        BoardResponseDTO dto = boardService.getBoardDetail(id, currentMemberId);
        return ResponseEntity.ok(dto);
    }

}
