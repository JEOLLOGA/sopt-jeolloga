package sopt.jeolloga.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://3.37.231.47",
                "https://www.gototemplestay.com",
                "https://api.gototemplestay.com"
        )); // 허용할 Origin
        config.setAllowedMethods(List.of("GET", "POST", "DELETE")); // 허용할 HTTP Method
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "refreshToken")); // 허용할 요청 헤더
        config.setExposedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "userId",
                "refreshToken"
        )); // 브라우저 접근 허용할 헤더 값들
        config.setAllowCredentials(true); // 쿠키/인증 정보를 포함하도록 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 모든 경로에 대해 CORS 설정 적용
        return source;
    }
}