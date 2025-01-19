package sopt.jeolloga.domain.member.api.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import sopt.jeolloga.domain.member.core.CustomOAuth2User;
import sopt.jeolloga.domain.member.api.utils.JwtTokenProvider;
import sopt.jeolloga.domain.member.core.Member;
import sopt.jeolloga.domain.member.core.MemberRepository;
import org.springframework.data.redis.core.RedisTemplate;


import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public CustomOAuth2UserService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider, RedisTemplate redisTemplate){
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
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

        Member member = findOrCreateUser(Long.parseLong(kakaoUserId), email, nickname);

        String accessToken = jwtTokenProvider.createAccessToken(kakaoUserId);
        String refreshToken = jwtTokenProvider.createRefreshToken(kakaoUserId);

        // redis에 refreshToken 업데이트
        jwtTokenProvider.deleteRefreshToken((Long) attributes.get("id"));
        jwtTokenProvider.saveRefreshToken((Long) attributes.get("id"), refreshToken);

        return new CustomOAuth2User(oAuth2User.getAuthorities(), attributes, "id", email, accessToken, refreshToken);
    }

    private Member findOrCreateUser(Long kakaoUserId, String email, String nickname) {

        return memberRepository.findByKakaoUserId(kakaoUserId)
                .orElseGet(() -> {
                    Member newMember = new Member(kakaoUserId, email, nickname);
                    memberRepository.save(newMember);
                    System.out.println("New User Created");
                    return newMember;
                });
    }
}


