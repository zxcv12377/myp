package com.example.server.security;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.server.dto.LoginRequestDTO;
import com.example.server.dto.RegisterRequestDTO;
import com.example.server.exception.CustomException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtProvider;

    // @PostMapping("/register")
    // public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDTO
    // dto) {
    // try {
    // authService.register(dto);
    // return ResponseEntity
    // .status(HttpStatus.CREATED)
    // .body("인증 메일을 발송했습니다. 메일함을 확인해주세요.");
    // } catch (IllegalArgumentException | CustomException e) {
    // log.warn("Register failed: {}", e.getMessage());
    // return ResponseEntity
    // .badRequest()
    // .body(e.getMessage());
    // } catch (Exception e) {
    // log.error("Unexpected error in register()", e);
    // return ResponseEntity
    // .status(HttpStatus.INTERNAL_SERVER_ERROR)
    // .body("서버 오류로 회원가입에 실패했습니다.");
    // }
    // }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO dto) {
        String token = authService.register(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(token);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam("token") String token) {
        try {
            String result = authService.verify(token);
            if ("DUPLICATE".equals(result)) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("이미 가입된 이메일입니다");
            }
            return ResponseEntity.ok("이메일 인증 성공: " + result);
        } catch (CustomException e) {
            log.warn("Verification failed: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error in verify()", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류로 인증에 실패했습니다.");
        }

    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequestDTO dto) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        String jwt = jwtProvider.generateToken(auth);
        return ResponseEntity.ok(Map.of("token", jwt));
    }
}