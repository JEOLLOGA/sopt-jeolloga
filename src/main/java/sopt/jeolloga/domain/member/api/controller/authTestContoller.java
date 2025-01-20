package sopt.jeolloga.domain.member.api.controller;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.domain.member.api.service.TokenService;
import sopt.jeolloga.domain.member.api.utils.JwtTokenProvider;
import sopt.jeolloga.domain.member.core.Token;
import sopt.jeolloga.domain.member.core.TokenRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
public class authTestContoller {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    public authTestContoller(JwtTokenProvider jwtTokenProvider, TokenService tokenService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenService = tokenService;
    }

    @PostMapping("/test/redis/save")
    public String saveToken() {

        String id = "2";
        String refreshToken = "test token2";
        tokenService.saveToken(id, refreshToken);
        return "Token saved!";
    }

    @GetMapping("/test/redis/get")
    public Token getToken() {
        String id = "1";
        return tokenService.getTokenById(id);
    }

    @DeleteMapping("/test/redis/delete")
    public String deleteToken() {
        tokenService.deleteToken("1");
        return "Token deleted!";
    }



    @PostMapping("/test/authentication")
    public ResponseEntity<Map<String, Object>> publicTest() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> result = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            // 인증 정보가 없는 경우 처리 (토큰 없음)
            result.put("None", "Token is missing. Accessing as an unauthenticated user.");
            return ResponseEntity.ok(result);
        }

        result.put("username", authentication.getName());
        result.put("authorities", authentication.getAuthorities());
        result.put("credentials", authentication.getCredentials());
        result.put("detail", authentication.getDetails());
        result.put("principal", authentication.getPrincipal());
        result.put("isAuthenticated", authentication.isAuthenticated());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/user/test")
    public ResponseEntity<Map<String, Object>> userTest() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> result = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            // 인증 정보가 없는 경우 처리 (토큰 없음)
            result.put("None", "Token is missing. Accessing as an unauthenticated user.");
            return ResponseEntity.ok(result);
        }

        result.put("username", authentication.getName());
        result.put("authorities", authentication.getAuthorities());
        result.put("credentials", authentication.getCredentials());
        result.put("detail", authentication.getDetails());
        result.put("principal", authentication.getPrincipal());
        result.put("isAuthenticated", authentication.isAuthenticated());

        return ResponseEntity.ok(result);
    }

    // 토큰 발급 테스트 - ok
    @GetMapping("/test/jwt")
    public String testJwt(@RequestParam String kakaoUserId) {
        String accessToken = jwtTokenProvider.createAccessToken(kakaoUserId);
        String refreshToken = jwtTokenProvider.createRefreshToken(kakaoUserId);
        return String.format("Access Token: %s%nRefresh Token: %s", accessToken, refreshToken);
    }

    // 토큰에서 id 추출 - ok
    @GetMapping("/test/getUserId")
    public String getKakaoId(@RequestParam String accessToken) {
        return String.format("kakao ID %s", jwtTokenProvider.getMemberIdFromToken(accessToken));
    }
}
