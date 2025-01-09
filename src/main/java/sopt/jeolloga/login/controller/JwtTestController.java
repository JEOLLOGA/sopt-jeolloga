package sopt.jeolloga.login.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sopt.jeolloga.config.JwtTokenProvider;

@RestController
public class JwtTestController {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTestController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/test/jwt")
    public String testJwt(@RequestParam Long userId) {
        String accessToken = jwtTokenProvider.createAccessToken(userId);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);
        return String.format("Access Token: %s%nRefresh Token: %s", accessToken, refreshToken);
    }
}
