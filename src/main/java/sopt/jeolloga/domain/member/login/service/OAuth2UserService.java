package sopt.jeolloga.domain.member.login.service;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Service;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    // Spring Security가 외부에서 가져온 사용자 정보를 OAuth2User 객체로 매핑
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User user = super.loadUser(userRequest);
        // Add custom logic (e.g., save user to DB)
        return user;
    }
}
