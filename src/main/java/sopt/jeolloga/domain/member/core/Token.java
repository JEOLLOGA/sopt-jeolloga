package sopt.jeolloga.domain.member.core;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "token", timeToLive = 60 * 60 * 24 * 14)
public class Token {
    @Id
    private String id;
    private String refreshToken;

    public Token(String id, String refreshToken) {
        this.id = id;
        this.refreshToken = refreshToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}