package sopt.jeolloga.domain.member.api.service;

import org.springframework.stereotype.Service;
import sopt.jeolloga.domain.member.api.utils.JwtTokenProvider;
import sopt.jeolloga.domain.member.core.MemberRepository;

@Service
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    public AuthService(JwtTokenProvider jwtTokenProvider, MemberRepository memberRepository){
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberRepository = memberRepository;
    }

    public String createAccessToken(String kakaoUserID){
        return jwtTokenProvider.createAccessToken(kakaoUserID);
    }

    public String createRefreshToken(String kakaoUserID){
        return jwtTokenProvider.createRefreshToken(kakaoUserID);
    }

    public boolean isTokenInDatabase(String refreshToken){
        // DB에 존재하는지 조회 + refreshToken
        return true;
    }

    // Refresh Token을 이용해 새로운 Access Token 발급
    public String refreshAccessToken(String refreshToken) {

        // Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired Refresh Token"); // refresh Token 만료 -> 재로그인 필요
        }

        // 사용자 kakao ID 추출
        String kakaoUserId = jwtTokenProvider.getMemberIdFromToken(refreshToken);
        System.out.println("kakaoUserID : " + kakaoUserId);

        // 사용자 유효성 확인
        memberRepository.findByKakaoUserId(Long.parseLong(kakaoUserId))
                .orElseThrow(() -> new IllegalArgumentException("Member not found with kakaoUserId: " + kakaoUserId));


        // DB에 사용자의 refreshToken과 입력된 refresh Token 일치하는지 확인할 필요 있음
        if (!isTokenInDatabase(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token is contaminated"); // refresh Token 만료 -> 재로그인 필요
        }

        // 새로운 Access Token 발급
        return jwtTokenProvider.createAccessToken(kakaoUserId);

    }

    public void saveRefreshToken(Long userId, String refreshToken) {
        // DB에 refreshToken 저장
        // refreshTokenRepository.save(userId, refreshToken);
    }

    // 로그아웃 관련
    public void deleteRefreshToken(Long userId) {
        // DB에서 Refresh Token 제거
        // refreshTokenRepository.deleteByUserId(userId);
    }
}
