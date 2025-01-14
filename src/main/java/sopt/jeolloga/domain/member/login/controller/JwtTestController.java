package sopt.jeolloga.domain.member.login.controller;

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

    @GetMapping("/test/validate/refresh")
    public boolean validateRefreshToken(@RequestParam String refreshToken) {
        // RefreshToken 검증
        return jwtTokenProvider.validateRefreshToken(refreshToken);
    }

    @GetMapping("/test/validate/access")
    public boolean validateAccessToken(@RequestParam String accessToken) {
        // AccessToken 검증
        return jwtTokenProvider.validateAccessToken(accessToken);
    }


    @GetMapping("/protected/test")
    public String protectedEndpoint() {
        return "You are authenticated!";
    }
}
