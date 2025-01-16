package sopt.jeolloga.domain.member.api.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import sopt.jeolloga.domain.member.api.utils.JwtTokenProvider;
import sopt.jeolloga.domain.member.core.Member;
import sopt.jeolloga.domain.member.core.MemberRepository;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public CustomOAuth2UserService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider){
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        // 기본 사용자 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 사용자 정보 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        // Kakao 사용자 정보 추출
        String kakaoUserId = (String) attributes.get("id"); // 카카오 ID
        String email = (String) kakaoAccount.get("email"); // email
        String nickname = (String) properties.get("nickname"); // nickname

        // 사용자 정보를 데이터베이스에 저장하거나 추가 처리
        Member member = findOrCreateUser(Long.parseLong(kakaoUserId), email, nickname);

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(kakaoUserId);
        String refreshToken = jwtTokenProvider.createRefreshToken(kakaoUserId);

        // CustomOAuth2User를 반환
        return new CustomOAuth2User(oAuth2User.getAuthorities(), attributes, "id", email, accessToken, refreshToken);
    }


    private Member findOrCreateUser(Long kakaoUserId, String email, String nickname) {
        // 새로운 유저 정보 저장
//        System.out.println("Saving or updating user: " + email + ", " + nickname);

        return memberRepository.findByKakaoUserId(kakaoUserId)
                .orElseGet(() -> {
                    Member newMember = new Member(kakaoUserId, email, nickname);
                    memberRepository.save(newMember);
                    System.out.println("New User Created");
                    return newMember;
                });
    }
}


