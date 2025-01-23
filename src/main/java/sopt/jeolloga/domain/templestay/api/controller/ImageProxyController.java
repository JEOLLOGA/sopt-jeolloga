package sopt.jeolloga.domain.templestay.api.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class ImageProxyController {
    @GetMapping("/image-proxy")
    public ResponseEntity<byte[]> getImageProxy(@RequestParam String imgUrl) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Referer", "https://www.naver.com");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(imgUrl, HttpMethod.GET, entity, byte[].class);

        return ResponseEntity.ok()
                .contentType(response.getHeaders().getContentType())
                .body(response.getBody());
    }
}
