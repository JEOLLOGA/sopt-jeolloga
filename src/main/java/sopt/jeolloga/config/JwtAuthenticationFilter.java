package sopt.jeolloga.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sopt.jeolloga.domain.member.api.utils.JwtTokenProvider;
import sopt.jeolloga.domain.member.core.exception.CustomAuthenticationCoreException;
import sopt.jeolloga.exception.ErrorCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/public/") || path.startsWith("/auth/") || path.startsWith("/login/");
        // "/protected/**" 경로가 아닌 경우 필터를 실행하지 않음
        return !path.startsWith("/protected/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        // Authorization 헤더에서 JWT 토큰 추출
        String accessToken = jwtTokenProvider.resolveToken(request);

        // token 존재
        if(accessToken != null) {

            if(jwtTokenProvider.validateToken(accessToken)) {
                // 유효한 토큰 -> SecurityContext에 인증 정보 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // 유효하지 않은 토큰 -> 401 응답 반환
                throw new CustomAuthenticationCoreException(ErrorCode.UNAUTHORIZED);
            }
        }

        // 토큰이 없으면 SecurityContext에 인증 정보를 설정하지 않고 다음 필터/비즈니스 로직으로 전달
        chain.doFilter(request, response);

        // header에서 token 가져옴
        String authHeader = request.getHeader("Authorization");

        // jwt 토큰 없거나 형식이 다르면 401 Unauthorized 반환
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Missing or invalid Authorization header\"}");
            return;
        }

        // jwt에서 토큰 추출
        String token = authHeader.substring(7);

        // 토큰 검증 및 사용자 정보 설정
        if(jwtTokenProvider.validateAccessToken(token)){


            Claims claims = jwtTokenProvider.getClaimsFromToken(token);

            // Spring Security 인증 객체 생성
            // 커스텀으로 설정할 수도 있음
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(), // Principal: 사용자 ID
                    null, // Credentials: 보통 null로 설정
                    null // Authorities: 권한 정보가 없으면 null
            );
            // SecurityContext에 인증 정보 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } else {
            // 401 Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Invalid or expired token\"}");
            return;
        }

        chain.doFilter(request, response);

    }
}
