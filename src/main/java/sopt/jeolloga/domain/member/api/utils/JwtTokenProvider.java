package sopt.jeolloga.domain.member.api.utils;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import sopt.jeolloga.domain.member.core.exception.CustomAuthenticationCoreException;
import sopt.jeolloga.exception.ErrorCode;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;


@Component
public class JwtTokenProvider { // Jwt Token 생성

    private final Key secretKey;
    private final long accessTokenValidity = 1 * 60 * 60 * 1000; // 1시간
    private final long refreshTokenValidity = 14 * 24 * 60 * 60 * 1000; // 14일

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // Access Token 발급
    public String createAccessToken(String memberId) {
        return createToken(memberId, this.accessTokenValidity);
    }

    // Refresh Token 발급
    public String createRefreshToken(String memberId) {

        String refreshToken = createToken(memberId, this.refreshTokenValidity);

        // Redis에 저장하는 로직 필요

        return refreshToken;
    }

    private String createToken(String memberId, long validity) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validity);

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .claim("roles", List.of("ROLE_USER"))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(this.secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 공통 토큰 검증 메서드
    public boolean validateToken(String token) {

        // 추후 에러코드 수정 필요
        try {
            Jwts.parserBuilder()
                    .setSigningKey(this.secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new CustomAuthenticationCoreException(ErrorCode.UNAUTHORIZED);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            throw new CustomAuthenticationCoreException(ErrorCode.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty");
        }
        return false;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        String memberId = claims.getSubject();

        // roles 클레임에서 권한 추출
        List<String> roles = claims.get("roles", List.class);

        // 고정된 권한 생성
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(memberId, null, authorities);
    }

    // JWT에서 사용자 kakaoUserID 추출
    public String getMemberIdFromToken(String token) {

        String userId =  Jwts.parserBuilder()
                .setSigningKey(this.secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        return userId;
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
