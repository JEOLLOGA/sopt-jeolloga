package sopt.jeolloga.config;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;


@Component
public class JwtTokenProvider { // Jwt Token 생성

    private final Key accessTokenKey;
    private final Key refreshTokenKey;
    private final long accessTokenValidity = 1 * 60 * 60 * 1000; // 1시간
    private final long refreshTokenValidity = 14 * 24 * 60 * 60 * 1000; // 14일

    public JwtTokenProvider(
            @Value("${jwt.access-token-secret}") String accessTokenSecret,
            @Value("${jwt.refresh-token-secret}") String refreshTokenSecret) {
        this.accessTokenKey = Keys.hmacShaKeyFor(accessTokenSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshTokenKey = Keys.hmacShaKeyFor(refreshTokenSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Access Token 발급
    public String createAccessToken(Long userId) { // kakaoUserId 기반으로 토큰 생성할 예정
        return createToken(userId, accessTokenKey, accessTokenValidity);
    }

    // Refresh Token 발급
    public String createRefreshToken(Long userId) {
        return createToken(userId, refreshTokenKey, refreshTokenValidity);
    }

    private String createToken(Long userId, Key key, long validity) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        // 디버깅 로그
        System.out.println("Token issued at: " + now);
        System.out.println("Token expires at: " + expiry);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Access Token 검증
    public boolean validateAccessToken(String token) {
        return validateToken(token, accessTokenKey);
    }

    // Refresh Token 검증
    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshTokenKey);
    }

    // 공통 토큰 검증 메서드
    private boolean validateToken(String token, Key key) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // JWT에서 사용자 kakaoUserID 추출
    public Long getUserIdFromToken(String token, boolean isAccessToken) {
        Key key = isAccessToken ? accessTokenKey : refreshTokenKey;
        String userID =  Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        return Long.parseLong(userID);
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessTokenKey) // Access Token 키 사용
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
