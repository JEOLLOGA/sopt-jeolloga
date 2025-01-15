package sopt.jeolloga.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sopt.jeolloga.domain.member.core.exception.AccessTokenNotFound;
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
        return path.startsWith("/public/") || path.startsWith("/auth/") || path.startsWith("/test/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // accessToken 존재하지 않는 경우
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AccessTokenNotFound(ErrorCode.MISSING_ACCESS_TOKEN);
        }


        String accessToken = authHeader.substring(7);

        if (jwtTokenProvider.validateAccessToken(accessToken)) {
            // accessToken 유효한 경우
            setAuthentication(accessToken);
            chain.doFilter(request, response);
        } else {
            // accessToken 유효하지 않은 경우
            sendUnauthorized(response, "Access Token is invalid or expired");
        }
    }

    private void setAuthentication(String token) {
        Claims claims = jwtTokenProvider.getClaimsFromToken(token);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                claims.getSubject(), null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + message + "\"}");
    }
//
//    private void handleUserRequest(String authHeader, HttpServletResponse response, FilterChain chain, HttpServletRequest request) throws IOException, ServletException {
//
//        // 토큰 전달 여부 판정
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            sendUnauthorized(response, "Access Token missing or invalid"); // Unauthorized
//            return;
//        }
//
//        // jwt 추출
//        String accessToken = authHeader.substring(7);
//
//        if(jwtTokenProvider.validateRefreshToken(accessToken)) {
//            // accessToken이 유효함
//            setAuthentication(accessToken);
//            chain.doFilter(request, response);
//        } else {
//
//
//        }
//
//    }
//
//
//
//        // jwt에서 토큰 추출
//        String token = authHeader.substring(7);
//
//        // 토큰 검증 및 사용자 정보 설정
//        if(jwtTokenProvider.validateAccessToken(token)){
//
//            Claims claims = jwtTokenProvider.getClaimsFromToken(token);
//
//            // Spring Security 인증 객체 생성
//            // 커스텀으로 설정할 수도 있음
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                    claims.getSubject(), // Principal: 사용자 ID
//                    null, // Credentials: 보통 null로 설정
//                    null // Authorities: 권한 정보가 없으면 null
//            );
//            // SecurityContext에 인증 정보 설정
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        } else {
//            // 401 Unauthorized
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
//            response.setContentType("application/json");
//            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Invalid or expired token\"}");
//            return;
//        }
//
//        chain.doFilter(request, response);
//    }
}
