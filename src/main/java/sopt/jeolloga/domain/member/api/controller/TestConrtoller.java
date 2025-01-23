package sopt.jeolloga.domain.member.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sopt.jeolloga.domain.member.api.utils.JwtTokenProvider;

@RestController
public class TestConrtoller {

    JwtTokenProvider jwtTokenProvider;

    public TestConrtoller(JwtTokenProvider jwtTokenProvider){
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/public/getToken")
    public void saveInfo(@RequestParam(value = "id") String id) {

        String accessToken = jwtTokenProvider.createAccessToken(id);
        String refreshToken = jwtTokenProvider.createRefreshToken(id);

    }


}
