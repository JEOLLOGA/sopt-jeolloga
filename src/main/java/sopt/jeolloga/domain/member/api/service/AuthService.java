package sopt.jeolloga.domain.member.api.service;

import org.springframework.stereotype.Service;
import sopt.jeolloga.domain.member.api.dto.*;

@Service
public class AuthService {

    private final KakaoClientApi kakaoClientApi;
    private final MemberService memberService;

    public AuthService(KakaoClientApi kakaoClientApi, MemberService memberService){
        this.kakaoClientApi = kakaoClientApi;
        this.memberService = memberService;
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

    public KakaoUnlinkRes unlink(Long userId) {

        Long kakaoUserId = memberService.getUserKakaoId(userId);
        memberService.deleteMemberById(userId);
        KakaoUnlinkRes kakaoUnlinkRes = kakaoClientApi.unlink(kakaoUserId);
        return kakaoUnlinkRes;
    }
}
