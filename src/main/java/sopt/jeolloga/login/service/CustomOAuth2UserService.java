package sopt.jeolloga.login.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        // 기본 사용자 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 사용자 정보 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();
//        System.out.println("Attributes: " + attributes);

        // Kakao 사용자 정보 추출
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        // 이메일 추출
        String email = (String) kakaoAccount.get("email");

        // 닉네임 추출
        String nickname = (String) properties.get("nickname");

        // 사용자 정보를 데이터베이스에 저장하거나 추가 처리
        saveOrUpdateUser(email, nickname);

        // CustomOAuth2User를 반환
        return new CustomOAuth2User(oAuth2User.getAuthorities(), attributes, "id", email);
    }

    private void saveOrUpdateUser(String email, String nickname) {
        // DB 저장 로직 작성
        System.out.println("Saving or updating user: " + email + ", " + nickname);
        // 예: UserRepository를 통해 사용자 정보 저장
    }


}


