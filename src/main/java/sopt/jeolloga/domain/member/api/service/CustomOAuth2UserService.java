package sopt.jeolloga.domain.member.api.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import sopt.jeolloga.domain.member.api.dto.MemberRes;
import sopt.jeolloga.domain.member.core.*;
import sopt.jeolloga.domain.member.api.utils.JwtTokenProvider;
import org.springframework.data.redis.core.RedisTemplate;


import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;

    public CustomOAuth2UserService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider, TokenRepository tokenRepository){
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        String kakaoUserId = String.valueOf(attributes.get("id"));
        String email = (String) kakaoAccount.get("email");
        String nickname = (String) properties.get("nickname");

        // 유저 생성 or 조회
        Long userId = findOrCreateUser(Long.parseLong(kakaoUserId), email, nickname);

        // access, refresh token 발급
        String accessToken = jwtTokenProvider.createAccessToken(kakaoUserId);
        String refreshToken = jwtTokenProvider.createRefreshToken(kakaoUserId);

        // 기존 token 삭제
        if(getRefreshTokenById(kakaoUserId) != null){
            deleteRefreshToken(kakaoUserId);
        }

        // Redis에 Refresh Token 저장
        saveRefreshToken(kakaoUserId, refreshToken);

        return new CustomOAuth2User(oAuth2User.getAuthorities(), attributes, "id", email, accessToken, refreshToken, userId);
    }

    private Long findOrCreateUser(Long kakaoUserId, String email, String nickname) {
        return memberRepository.findByKakaoUserId(kakaoUserId)
                .map(Member::getId)
                .orElseGet(() -> {
                    Member newMember = new Member(kakaoUserId, email, nickname);
                    memberRepository.save(newMember);
                    System.out.println("New User Created");
                    return newMember.getId();
                });
    }

    public void saveRefreshToken(String id, String refreshToken) {
        Token token = new Token(id, refreshToken);
        tokenRepository.save(token);
    }

    public Token getRefreshTokenById(String id) {
        return tokenRepository.findById(id).orElse(null);
    }

    public void deleteRefreshToken(String id) {
        tokenRepository.deleteById(id);
    }

}


