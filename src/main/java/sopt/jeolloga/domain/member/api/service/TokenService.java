package sopt.jeolloga.domain.member.api.service;

import org.springframework.stereotype.Service;
import sopt.jeolloga.domain.member.api.utils.JwtTokenProvider;
import sopt.jeolloga.domain.member.core.RefreshToken;
import sopt.jeolloga.domain.member.core.RefreshTokenRepository;
import org.springframework.data.redis.core.RedisTemplate;
import sopt.jeolloga.domain.member.core.exception.MemberCoreException;
import sopt.jeolloga.exception.ErrorCode;

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

    public String createAccessToken(String userId){
        return jwtTokenProvider.createAccessToken(userId);
    }

    public String createRefreshToken(String userId){
        return jwtTokenProvider.createRefreshToken(userId);
    }

    public void updateRefreshToken(String userId, String refreshToken){

        if(getRefreshToken(userId) != null){
            deleteRefreshToken(userId);
        }
        saveRefreshToken(userId, refreshToken);
    }

    public String reissueAccessToken(String refreshToken){

        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        String oldRefreshToken = getRefreshToken(userId);

        // 저장된 refreshToken이 없는 경우
        if (oldRefreshToken == null) {
            throw new MemberCoreException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        // 입력된 refreshToken이 저장된 토큰과 일치하지 않는 경우
        if (!oldRefreshToken.equals(refreshToken)) {
            throw new MemberCoreException(ErrorCode.INVALID_TOKEN);
        }

        return jwtTokenProvider.createAccessToken(userId);
    }

    public void logout(String accessToken){

        String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        deleteRefreshToken(userId);
    }


    // refresh Token 관련

    // Refresh Token 저장
    public void saveRefreshToken(String userId, String token) {
        RefreshToken refreshToken = new RefreshToken(userId, token);
        refreshTokenRepository.save(refreshToken);
    }

    // Refresh Token 조회
    public String getRefreshToken(String userId) {
        return refreshTokenRepository.findById(userId)
                .map(RefreshToken::getRefreshToken)
                .orElse(null);
    }

    // Refresh Token 삭제
    public void deleteRefreshToken(String userId) {
        refreshTokenRepository.deleteById(userId);
    }

}