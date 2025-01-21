package sopt.jeolloga.domain.member.core;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 24 * 14)
public class RefreshToken {

    @Id
    private String kakaoUserId; // Redis 키
    private String refreshToken; // Refresh Token 값

    public RefreshToken(String kakaoUserId, String refreshToken) {
        this.kakaoUserId = kakaoUserId;
        this.refreshToken = refreshToken;
    }

    public String getKakaoUserId() {
        return kakaoUserId;
    }

    public void setKakaoUserId(String kakaoUserId) {
        this.kakaoUserId = kakaoUserId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
