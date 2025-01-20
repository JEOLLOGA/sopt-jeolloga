package sopt.jeolloga.domain.member.api.service;


import org.springframework.stereotype.Service;
import sopt.jeolloga.domain.member.core.Token;
import sopt.jeolloga.domain.member.core.TokenRepository;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public void saveToken(String id, String refreshToken) {
        Token token = new Token(id, refreshToken);
        tokenRepository.save(token); // 저장
    }

    public Token getTokenById(String id) {
        return tokenRepository.findById(id).orElse(null); // 조회
    }

    public void deleteToken(String id) {
        tokenRepository.deleteById(id); // 삭제
    }
}