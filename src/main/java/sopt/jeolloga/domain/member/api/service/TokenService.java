package sopt.jeolloga.domain.member.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import sopt.jeolloga.domain.member.api.utils.JwtTokenProvider;
import sopt.jeolloga.domain.member.core.exception.MemberCoreException;
import sopt.jeolloga.exception.ErrorCode;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    private static final long REFRESH_TOKEN_EXPIRATION_DAYS = 14;

    public String createAccessToken(String userId) {
        return jwtTokenProvider.createAccessToken(userId);
    }

    public String createRefreshToken(String userId) {
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);
        saveRefreshToken(userId, refreshToken);
        return refreshToken;
    }

    public void saveRefreshToken(String userId, String token) {
        redisTemplate.opsForValue().set("refreshToken:" + userId, token, REFRESH_TOKEN_EXPIRATION_DAYS, TimeUnit.DAYS);
    }

    public String getRefreshToken(String userId) {
        return redisTemplate.opsForValue().get("refreshToken:" + userId);
    }

    public void deleteRefreshToken(String userId) {
        redisTemplate.delete("refreshToken:" + userId);
    }

    public void updateRefreshToken(String userId, String newToken) {
        deleteRefreshToken(userId);
        saveRefreshToken(userId, newToken);
    }

    public String reissueAccessToken(String refreshToken) {
        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        String storedToken = getRefreshToken(userId);

        if (storedToken == null) {
            throw new MemberCoreException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        if (!storedToken.equals(refreshToken)) {
            throw new MemberCoreException(ErrorCode.INVALID_TOKEN);
        }

        return jwtTokenProvider.createAccessToken(userId);
    }

    public void logout(String accessToken) {
        String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        deleteRefreshToken(userId);
    }
}
