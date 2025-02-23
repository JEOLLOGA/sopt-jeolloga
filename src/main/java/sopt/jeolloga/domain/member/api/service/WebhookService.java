package sopt.jeolloga.domain.member.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import sopt.jeolloga.domain.member.core.MemberRepository;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class WebhookService {
    private final MemberRepository memberRepository;
    @Value("${discord.webhook.url}")
    private String discordWebhookUrl;

    public String sendDiscordNotification() {
        RestTemplate restTemplate = new RestTemplate();
        Long totalMembers = memberRepository.count();

        String msg = totalMembers + "번째 사용자가 회원가입했습니다!\n";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("content", msg);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(discordWebhookUrl, requestEntity, String.class);

        return null;
    }
}
