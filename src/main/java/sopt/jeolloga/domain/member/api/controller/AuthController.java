package sopt.jeolloga.domain.member.api.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.domain.member.api.utils.JwtTokenProvider;
import sopt.jeolloga.domain.member.api.service.AuthService;

@RestController
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // refresh 토큰 기반 access 토큰 재발급
    @GetMapping("/auth/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader String refreshToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + authService.refreshAccessToken(refreshToken));

        return ResponseEntity.ok().headers(headers).body(null);
    }

    // 최초 로그인?
    @GetMapping("/auth/login")
    public ResponseEntity<?> login(@RequestParam String userId) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + authService.createAccessToken(userId));
        headers.add("Refresh-Token", authService.createRefreshToken(userId));

        return ResponseEntity.ok().headers(headers).body(null);
    }

    //


}
