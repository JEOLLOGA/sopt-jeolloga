package sopt.jeolloga.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import sopt.jeolloga.domain.member.core.CustomOAuth2User;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();

        response.setHeader("Authorization", "Bearer " + user.getAccessToken());
        response.setHeader("refreshToken", user.getRefreshToken());
        response.setHeader("id", String.valueOf(user.getUserId()));
        response.setStatus(HttpServletResponse.SC_OK);

    }
}