package sopt.jeolloga.domain.member.login.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sopt.jeolloga.domain.member.login.dto.JwtResponse;
import sopt.jeolloga.domain.member.login.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestParam String refreshToken) {
        try {
            // Service를 통해 Access Token 갱신
            String newAccessToken = authService.refreshAccessToken(refreshToken);

            // 클라이언트에 갱신된 Access Token과 Refresh Token 반환
            return ResponseEntity.ok(new JwtResponse(newAccessToken, refreshToken));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
