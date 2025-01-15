package sopt.jeolloga.domain.member.login.service;

import org.springframework.stereotype.Service;
import sopt.jeolloga.config.JwtTokenProvider;
import sopt.jeolloga.domain.member.login.repository.MemberRepository;

@Service
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    public AuthService(JwtTokenProvider jwtTokenProvider, MemberRepository memberRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberRepository = memberRepository;
    }

    // Refresh Token을 이용해 새로운 Access Token 발급
    public String refreshAccessToken(String refreshToken) {

        // Refresh Token 검증
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired Refresh Token"); // refresh Token 만료 -> 재로그인 필요
        }

        // 사용자 ID 추출
        Long kakaoUserId = jwtTokenProvider.getUserIdFromToken(refreshToken, false);

        // 사용자 유효성 확인
        memberRepository.findById(kakaoUserId).orElseThrow(() ->
                new IllegalArgumentException("User not found"));


        // DB에 사용자의 refreshToken과 입력된 refresh Token 일치하는지 확인할 필요 있음


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
