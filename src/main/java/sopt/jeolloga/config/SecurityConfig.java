package sopt.jeolloga.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sopt.jeolloga.domain.member.api.service.CustomOAuth2UserService;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import sopt.jeolloga.domain.member.api.utils.CustomAuthenticationEntryPoint;

@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    public SecurityConfig(
            CustomOAuth2UserService customOAuth2UserService,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {

        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/**").authenticated() // /user 경로 인증 필요
                        .requestMatchers("/public/**").permitAll() // /public 경로 허용
                        .requestMatchers("/login/**").permitAll() // /public 경로 허용
                        .requestMatchers("/auth/**").permitAll() // refresh 토큰 기반 토큰 재발급 api
                        .anyRequest().permitAll() // 나머지 경로는 기본적으로 허용
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .oauth2Login(oauth2 -> oauth2
                                .loginPage("/login") // 명시적으로 로그인 페이지 설정
                                // OAuth2 인증 요청
                                .authorizationEndpoint(auth -> auth
                                        .authorizationRequestRepository(new HttpSessionOAuth2AuthorizationRequestRepository()) // 저장소 설정
                                )
                                // 유저 정보 가져오기
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(customOAuth2UserService)
                                )
                                .successHandler(oAuth2LoginSuccessHandler) // 로그인 성공 핸들러
//                        .defaultSuccessUrl("/login/success") // 로그인 성공 시 리다이렉트
                                .failureUrl("/login/failure") // 로그인 실패 시 리다이렉트
                )
                .formLogin(form -> form
                        .loginPage("/login").permitAll() // Spring Security의 기본 로그인 페이지 사용
                );
        return http.build();
    }

//    @Bean
//    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
//        return new CustomAuthenticationEntryPoint();
//    }
}




