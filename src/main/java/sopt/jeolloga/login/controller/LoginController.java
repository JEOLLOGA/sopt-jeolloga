package sopt.jeolloga.login.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import sopt.jeolloga.login.service.CustomOAuth2User;

import java.util.Map;

@RestController
public class LoginController {

    @GetMapping("/login/success")
    public String loginSuccess(OAuth2AuthenticationToken authentication) {

        // 사용자 정보 가져오기
        Map<String, Object> attributes = authentication.getPrincipal().getAttributes();

        // Kakao 사용자 정보 추출
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) properties.get("nickname");

        return "Welcome, " + nickname + "! Your email is: " + email;
    }


    @GetMapping("/login/failure")
    public String loginFailure() {
        return "Login Failed!";
    }
}
