package sopt.jeolloga.domain.member.api.service;

import org.springframework.stereotype.Service;
import sopt.jeolloga.domain.member.api.dto.KakaoTokenRes;
import sopt.jeolloga.domain.member.api.dto.KakaoUserInfoRes;
import sopt.jeolloga.domain.member.api.dto.LoginRes;
import sopt.jeolloga.domain.member.api.dto.MemberRes;

@Service
public class AuthService {

    private final KakaoClientApi kakaoClientApi;

    public AuthService(KakaoClientApi kakaoClientApi){
        this.kakaoClientApi = kakaoClientApi;
    }

    public String getAccessToken(String redirectUri, String code) {
        KakaoTokenRes kakaoTokenRes = kakaoClientApi.getAccessToken(redirectUri, code);
        return kakaoTokenRes.access_token();
    }

    public MemberRes getUserInfo(String accessToken) {
        KakaoUserInfoRes kakaoUserInfo = kakaoClientApi.getUserInfo(accessToken);
        MemberRes memberRes = new MemberRes(kakaoUserInfo.id(), kakaoUserInfo.kakao_account().profile().nickname() ,kakaoUserInfo.kakao_account().email());
        return memberRes;
    }
}
