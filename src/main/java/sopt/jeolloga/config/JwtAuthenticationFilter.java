package sopt.jeolloga.config;

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
    }
}
