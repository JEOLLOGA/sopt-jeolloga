package sopt.jeolloga.domain.member.api.service;


import io.jsonwebtoken.Jwt;
import org.springframework.stereotype.Service;
import sopt.jeolloga.domain.member.api.utils.JwtTokenProvider;
import sopt.jeolloga.domain.member.core.Token;
import sopt.jeolloga.domain.member.core.TokenRepository;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenService(TokenRepository tokenRepository, JwtTokenProvider jwtTokenProvider) {
        this.tokenRepository = tokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String reIssueAccessToken(String refreshToken) {

        String kakaoUserId = jwtTokenProvider.getMemberIdFromToken(refreshToken);
        String storedRefreshToken = getTokenById(kakaoUserId).getRefreshToken();

        if (storedRefreshToken == null) {
            throw new IllegalArgumentException("Refresh Token이 존재하지 않습니다."); // refreshToken 만료
        } else if(!storedRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("잘못된 refresh Token이 입력되었습니다"); // refreshToken 불일치
        }

        return jwtTokenProvider.createAccessToken(kakaoUserId);
    }

    public void saveToken(String id, String refreshToken) {
        Token token = new Token(id, refreshToken);
        tokenRepository.save(token);
    }

    public Token getTokenById(String id) {
        return tokenRepository.findById(id).orElse(null);
    }

    public void deleteToken(String id) {
        tokenRepository.deleteById(id);
    }
}