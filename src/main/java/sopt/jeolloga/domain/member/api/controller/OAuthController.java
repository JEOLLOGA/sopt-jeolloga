package sopt.jeolloga.domain.member.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import sopt.jeolloga.domain.member.api.dto.LoginRes;
import sopt.jeolloga.domain.member.api.dto.MemberRes;
import sopt.jeolloga.domain.member.api.service.MemberService;
import sopt.jeolloga.domain.member.api.service.OAuthService;
import sopt.jeolloga.domain.member.api.service.TokenService;

import java.util.Map;

@Controller
public class OAuthController {

    private OAuthService oAuthService;
    private MemberService memberService;
    private TokenService tokenService;

    @Value("${kakao.client.client-id}")
    private String client_id;

    @Value("${kakao.client.redirect-uri}")
    private String redirect_uri;

    public OAuthController(OAuthService oAuthService, MemberService memberService, TokenService tokenService){
        this.oAuthService = oAuthService;
        this.memberService = memberService;
        this.tokenService = tokenService;
    }

    @GetMapping("/login/page")
    public String loginPage(Model model) {
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + client_id + "&redirect_uri=" + redirect_uri;
        model.addAttribute("location", location);
        return "login";
    }

    @GetMapping("/login")
    public ResponseEntity<LoginRes> login(@RequestParam("code") String code) {

        // accessToken 발급 받아오기
        String kakaoAccessToken = oAuthService.getKakaoAccessToken(code);

        // accessToken 기반으로 유저 정보 받아오기
        MemberRes memberInfo = oAuthService.getKakaoUserInfo(kakaoAccessToken);
        String kakaoUserId = String.valueOf(memberInfo.userId());

        // kakao에서 받아온 유저정보 바탕으로 멤버 생성 or id 조회
        LoginRes loginRes = memberService.findOrCreateUser(memberInfo);

        // accessToken, refreshToken 생성
        String accessToken = tokenService.createAccessToken(kakaoUserId);
        String refreshToken = tokenService.createRefreshToken(kakaoUserId);

        // refreshToken 저장
        tokenService.updateRefreshToken(kakaoUserId, refreshToken);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken)
                .header("refreshToken", refreshToken)
                .body(loginRes);
    }

    @GetMapping("/auth/refresh")
    public ResponseEntity<?> reissueAccessToken(@RequestHeader("refreshToken") String refreshToken) {

        // AccessToken 재발급
        String accessToken = tokenService.reissueAccessToken(refreshToken);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }


    @PostMapping("logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorization, HttpServletRequest request) {

        String accessToken = authorization.substring(7);
        tokenService.logout(accessToken);

        return ResponseEntity.ok("refreshToken deleted!");
    }
}
