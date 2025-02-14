package sopt.jeolloga.domain.member.api.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import sopt.jeolloga.domain.member.api.dto.KakaoTokenRes;
import sopt.jeolloga.domain.member.api.dto.KakaoUnlinkRes;
import sopt.jeolloga.domain.member.api.dto.KakaoUserInfoRes;

@Component
@RequiredArgsConstructor
public class KakaoClientApi implements OAuthClientApi {

    @Value("${kakao.client.client-id}")
    private String clientId;

    @Value("${kakao.client.admin-key}")
    private String adminKey;

    private final RestClient restClient;

    @Override
    public KakaoTokenRes getAccessToken(String redirectUri, String code){

        return restClient.method(HttpMethod.POST)
                .uri("https://kauth.kakao.com/oauth/token")
                .body("grant_type=authorization_code" +
                        "&client_id=" + clientId +
                        "&redirect_uri=" + redirectUri +
                        "&code=" + code)
                .retrieve()
                .toEntity(KakaoTokenRes.class)
                .getBody();
    }

    @Override
    public KakaoUserInfoRes getUserInfo(String accessToken) {

        return restClient.method(HttpMethod.POST)
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization","Bearer " + accessToken)
                .retrieve()
                .toEntity(KakaoUserInfoRes.class)
                .getBody();
    }
}
