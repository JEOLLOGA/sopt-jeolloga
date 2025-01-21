package sopt.jeolloga.domain.member.api.service;

import org.springframework.stereotype.Service;
import sopt.jeolloga.domain.member.api.utils.JwtTokenProvider;
import sopt.jeolloga.domain.member.core.RefreshToken;
import sopt.jeolloga.domain.member.core.RefreshTokenRepository;
import org.springframework.data.redis.core.RedisTemplate;

@Service
public class TokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    public TokenService(RefreshTokenRepository refreshTokenRepository, JwtTokenProvider jwtTokenProvider, RedisTemplate<String, Object> redisTemplate) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    public String createAccessToken(String kakaoUserId){
        return jwtTokenProvider.createAccessToken(kakaoUserId);
    }

    public String createRefreshToken(String kakaoUserId){
        return jwtTokenProvider.createRefreshToken(kakaoUserId);
    }

    public void updateRefreshToken(String kakaoUserId, String refreshToken){

        if(getRefreshToken(kakaoUserId) != null){
            deleteRefreshToken(kakaoUserId);
        }
        saveRefreshToken(kakaoUserId, refreshToken);
    }

    public String reissueAccessToken(String refreshToken){

        String kakaoUserId = jwtTokenProvider.getMemberIdFromToken(refreshToken);

        String oldRefreshToken = getRefreshToken(kakaoUserId);

        // 저장된 refreshToken이 없는 경우
        if (oldRefreshToken == null) {
            throw new IllegalArgumentException("토큰이 만료되어 재로그인이 필요합니다.");
        }

        // 입력된 refreshToken이 저장된 토큰과 일치하지 않는 경우
        if (!oldRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("잘못된 토큰이 입력되었습니다.");
        }

        return jwtTokenProvider.createAccessToken(kakaoUserId);
    }

    public void logout(String accessToken){

        String kakaoUserId = jwtTokenProvider.getMemberIdFromToken(accessToken);
        deleteRefreshToken(kakaoUserId);
    }



    // refresh Token 관련

    // Refresh Token 저장
    public void saveRefreshToken(String kakaoUserId, String token) {
        RefreshToken refreshToken = new RefreshToken(kakaoUserId, token);
        refreshTokenRepository.save(refreshToken);
    }

    // Refresh Token 조회
    public String getRefreshToken(String kakaoUserId) {
        return refreshTokenRepository.findById(kakaoUserId)
                .map(RefreshToken::getRefreshToken)
                .orElse(null);
    }

    // Refresh Token 삭제
    public void deleteRefreshToken(String kakaoUserId) {
        refreshTokenRepository.deleteById(kakaoUserId);
    }

}