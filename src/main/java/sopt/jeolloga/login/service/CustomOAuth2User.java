package sopt.jeolloga.login.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User extends DefaultOAuth2User {

    private final String accessToken;
    private final String refreshToken;
    private final String email;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey, String email, String accessToken, String refreshToken) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // 커스텀 메서드 추가
    public String getEmail() {
        return (String) getAttributes().get("email");
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getNickname() {
        Map<String, Object> properties = (Map<String, Object>) getAttributes().get("properties");
        return (String) properties.get("nickname");
    }
}
