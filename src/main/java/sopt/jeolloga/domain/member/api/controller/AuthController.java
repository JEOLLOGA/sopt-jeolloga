package sopt.jeolloga.domain.member.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.domain.member.api.dto.LoginReq;
import sopt.jeolloga.domain.member.api.dto.LoginRes;
import sopt.jeolloga.domain.member.api.dto.MemberRes;
import sopt.jeolloga.domain.member.api.service.AuthService;
import sopt.jeolloga.domain.member.api.service.MemberService;
import sopt.jeolloga.domain.member.api.service.TokenService;
import sopt.jeolloga.domain.member.core.MemberRepository;

@Controller
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;
    private final TokenService tokenService;

    @Value("${kakao.client.client-id}")
    private String CLIENT_ID;

    @Value("${kakao.client.redirect-uri}")
    private String REDIRECT_URI;

    public AuthController(AuthService authService, MemberService memberService, TokenService tokenService){
        this.authService = authService;
        this.memberService = memberService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginRes> login(@RequestBody LoginReq request){

        // 소셜 로그인 API 요청
        String socialAccessToken = authService.getAccessToken(request.redirectUri(), request.code());
        MemberRes member = authService.getUserInfo(socialAccessToken);

        // 멤버 생성 또는 조회
        LoginRes loginRes = memberService.findOrCreateUser(member);

        // 토큰 생성
        String userId = String.valueOf(loginRes.userId());
        String accessToken = tokenService.createAccessToken(userId);
        String refreshToken = tokenService.createRefreshToken(userId);
        tokenService.updateRefreshToken(userId, refreshToken);

        return ResponseEntity
                .ok()
                .header("Authorization", "Bearer " + accessToken)
                .header("refreshToken", refreshToken)
                .body(loginRes);
    }

    @GetMapping("/login/refresh")
    public ResponseEntity<?> reissueAccessToken(@RequestHeader("refreshToken") String refreshToken) {

        String accessToken = tokenService.reissueAccessToken(refreshToken);

        return ResponseEntity
                .ok()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.substring(7);
        tokenService.logout(accessToken);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/login/page")
    public void loginPage(Model model){
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI;
        model.addAttribute("location", location);
    }

    @GetMapping("login/code")
    public ResponseEntity<?> loginCode(@RequestParam("code") String code){
        return ResponseEntity.ok(code);
    }
}
