package sopt.jeolloga.domain.member.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.domain.member.api.service.CustomOAuth2UserService;
import sopt.jeolloga.domain.member.api.service.AuthService;

@RestController
public class AuthController {

    private final AuthService authService;
    private final CustomOAuth2UserService customOAuth2UserService;

    public AuthController(AuthService authService, CustomOAuth2UserService customOAuth2UserService) {
        this.authService = authService;
        this.customOAuth2UserService = customOAuth2UserService;
    }

    // refresh 토큰 기반 access 토큰 재발급
    @PostMapping("/auth/reissue")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader String refreshToken) {



        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + customOAuth2UserService.reissueAccessToken(refreshToken));

        return ResponseEntity.ok().headers(headers).body(null);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){

        String authorizationHeader = request.getHeader("Authorization");
        String accessToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
             authorizationHeader.substring(7);
        }

//        authService.logout(accessToken);

        return ResponseEntity.ok("logout");
    }
}
