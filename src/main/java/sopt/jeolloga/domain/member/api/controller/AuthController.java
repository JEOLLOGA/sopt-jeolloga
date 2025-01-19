package sopt.jeolloga.domain.member.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.domain.member.api.utils.JwtTokenProvider;
import sopt.jeolloga.domain.member.api.service.AuthService;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // refresh 토큰 기반 access 토큰 재발급
    @GetMapping("/auth/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader String refreshToken) {


        // refresh token 검증


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + authService.refreshAccessToken(refreshToken));

        return ResponseEntity.ok().headers(headers).body(null);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){


        String authorizationHeader = request.getHeader("Authorization");
        String accessToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
             authorizationHeader.substring(7);
        }

        authService.logout(accessToken);

        return ResponseEntity.ok("logout");
    }
}
