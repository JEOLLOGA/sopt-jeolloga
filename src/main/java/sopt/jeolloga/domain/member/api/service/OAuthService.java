package sopt.jeolloga.domain.member.api.service;

import io.netty.handler.codec.http.HttpHeaderValues;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import sopt.jeolloga.domain.member.api.dto.KakaoTokenRes;
import sopt.jeolloga.domain.member.api.dto.KakaoUserInfoRes;
import sopt.jeolloga.domain.member.api.dto.MemberRes;
import sopt.jeolloga.domain.member.api.utils.JwtTokenProvider;
import sopt.jeolloga.domain.member.core.MemberRepository;

@Service
public class OAuthService {

    @Value("${kakao.provider.KAUTH_TOKEN_URL_HOST}")
    private String KAUTH_TOKEN_URL_HOST;

    @Value("${kakao.provider.KAUTH_USER_URL_HOST}")
    private String KAUTH_USER_URL_HOST;

    @Value("${kakao.client.client-id}")
    private String CLIENT_ID;


    MemberRepository memberRepository;
    JwtTokenProvider jwtTokenProvider;

    public OAuthService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider){
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String getKakaoAccessToken(String authorizationCode, String redirect_uri){

        KakaoTokenRes kakaoTokenRes = WebClient.create(KAUTH_TOKEN_URL_HOST).post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .queryParam("redirect_uri", redirect_uri)
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", CLIENT_ID)
                        .queryParam("code", authorizationCode)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve() // 응답처리
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoTokenRes.class) // response를 KakaoTokenResponseDto 형태로 매핑
                .block(); // 비동기적 호출 (결과 반환까지 대기)

        return kakaoTokenRes.access_token();
    }



    public MemberRes getKakaoUserInfo(String accessToken){

        KakaoUserInfoRes kakaoUserInfo = WebClient.create(KAUTH_USER_URL_HOST)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoUserInfoRes.class)
                .block();

        MemberRes memberRes = new MemberRes(kakaoUserInfo.id(), kakaoUserInfo.kakao_account().profile().nickname() ,kakaoUserInfo.kakao_account().email());

        return memberRes;
    }

}
