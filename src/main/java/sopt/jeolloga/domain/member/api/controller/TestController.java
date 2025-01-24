package sopt.jeolloga.domain.member.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sopt.jeolloga.domain.member.api.utils.JwtTokenProvider;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    JwtTokenProvider jwtTokenProvider;

    public TestController(JwtTokenProvider jwtTokenProvider){
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/public/getToken")
    public ResponseEntity<?> saveInfo(@RequestParam(value = "id") String id) {

        String accessToken = jwtTokenProvider.createAccessToken(id);
        String refreshToken = jwtTokenProvider.createRefreshToken(id);

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);

        return ResponseEntity.ok(response);

    }
}
