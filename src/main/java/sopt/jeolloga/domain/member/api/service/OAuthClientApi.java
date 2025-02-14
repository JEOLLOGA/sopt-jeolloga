package sopt.jeolloga.domain.member.api.service;

import sopt.jeolloga.domain.member.api.dto.KakaoTokenRes;
import sopt.jeolloga.domain.member.api.dto.KakaoUnlinkRes;

public interface OAuthClientApi {
    Object getAccessToken(String redirectUri, String code);
    Object getUserInfo(String token);
//    Object unlink(Long userId);
}
