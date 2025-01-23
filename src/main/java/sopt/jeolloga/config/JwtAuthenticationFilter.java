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
import sopt.jeolloga.domain.member.core.exception.MemberCoreException;
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
        return path.startsWith("/public/") || path.startsWith("/login/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try{
            // Authorization 헤더에서 JWT 토큰 추출
            String accessToken = jwtTokenProvider.resolveToken(request);

            // token 존재
            if(accessToken != null) {

                if(jwtTokenProvider.validateToken(accessToken)) {
                    // 유효한 토큰
                    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // 유효하지 않은 토큰 (만료되거나 조작된 토큰)
                    throw new MemberCoreException(ErrorCode.UNAUTHORIZED);
                }
            }
            chain.doFilter(request, response);
        } catch (MemberCoreException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"data\": \"Unauthorized\", \"message\": \"" + e.getErrorCode().getMsg() + "\"}");
        }
    }
}
