package sopt.jeolloga.domain.member.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

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

        return "안녕하세요 " + nickname + "님";
    }


    @GetMapping("/login/failure")
    public String loginFailure() {
        return "Login Failed!";
    }
}
