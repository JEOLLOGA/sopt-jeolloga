package sopt.jeolloga.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import sopt.jeolloga.domain.member.api.service.CustomOAuth2User;
import sopt.jeolloga.domain.member.login.service.CustomOAuth2User;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();

        // JSON 응답으로 Access Token과 Refresh Token 반환
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format(
                "{\"accessToken\": \"%s\", \"refreshToken\": \"%s\"}",
                user.getAccessToken(),
                user.getRefreshToken()
        ));
    }
}