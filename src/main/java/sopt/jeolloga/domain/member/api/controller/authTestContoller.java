package sopt.jeolloga.domain.member.api.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.domain.member.api.utils.JwtTokenProvider;

import java.util.HashMap;
import java.util.Map;

@RestController
public class authTestContoller {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public authTestContoller(JwtTokenProvider jwtTokenProvider, RedisTemplate redisTemplate) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/test/redis")
    public ResponseEntity<?> checkRedis(){
        checkConnection();
        return ResponseEntity.ok("redis test");
    }


    public void checkConnection() {
        try {
            // 테스트 키 저장
            redisTemplate.opsForValue().set("testKey", "testValue");
            String value = redisTemplate.opsForValue().get("testKey");

            if ("testValue".equals(value)) {
                System.out.println("Redis is connected and working!");
            } else {
                System.out.println("Redis connection failed or misconfigured.");
            }
        } catch (Exception e) {
            System.err.println("Redis connection test failed: " + e.getMessage());
        }
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
