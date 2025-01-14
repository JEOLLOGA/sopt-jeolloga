package sopt.jeolloga.domain.templestay.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import sopt.jeolloga.domain.templestay.api.vo.NaverResultVO;
import sopt.jeolloga.domain.templestay.api.vo.TemplestayVO;
import sopt.jeolloga.domain.templestay.core.Templestay;
import sopt.jeolloga.domain.templestay.core.TemplestayRepository;
import sopt.jeolloga.domain.templestay.core.exception.TemplestayCoreException;
import sopt.jeolloga.domain.templestay.core.exception.TemplestayNotFoundException;
import sopt.jeolloga.exception.ErrorCode;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class ReviewApiService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewApiService.class);

    private final TemplestayRepository templestayRepository;

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    public List<TemplestayVO> getBlogsByTemplestayId(Long templestayId) {
        logger.info("Fetching templestay reviews for ID: {}", templestayId);

        Templestay templestay = templestayRepository.findById(templestayId)
                .orElseThrow(() -> new TemplestayNotFoundException());

        String templeName = templestay.getTempleName();
        logger.info("Temple name: {}", templeName);

        return fetchBlogsFromNaverApi(templeName);
    }

    private List<TemplestayVO> fetchBlogsFromNaverApi(String templeName) {
        logger.info("Fetching blogs from Naver API for temple name: {}", templeName);

        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/blog.json")
                .queryParam("query", templeName + " 템플스테이")
                .queryParam("display", 50)
                .queryParam("start", 1)
                .queryParam("sort", "sim")
                .encode()
                .build()
                .toUri();

        logger.info("Generated URI: {}", uri);

        RequestEntity<Void> requestEntity = RequestEntity
                .get(uri)
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .build();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        NaverResultVO resultVO;

        try {
            resultVO = objectMapper.readValue(response.getBody(), NaverResultVO.class);
        } catch (JsonProcessingException e) {
            logger.error("Error while processing Naver API response", e);
            throw new TemplestayCoreException(ErrorCode.JSON_FIELD_ERROR);
        }

        // 네이버 블로그 링크만 필터링
        List<TemplestayVO> filteredItems = resultVO.getItems().stream()
                .filter(item -> item.getLink().contains("https://blog.naver.com"))
                .toList();

        for (TemplestayVO item : filteredItems) {
            item.setThumbnail(fetchFirstImageFromBlog(item.getLink()));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        return filteredItems.stream()
                .sorted((o1, o2) -> {
                    LocalDate date1 = LocalDate.parse(o1.getPostdate(), formatter);
                    LocalDate date2 = LocalDate.parse(o2.getPostdate(), formatter);
                    return date2.compareTo(date1);
                })
                .toList();
    }

    private String fetchFirstImageFromBlog(String blogUrl) {
        try {
            Document document = Jsoup.connect(blogUrl).get();

            Element iframe = document.selectFirst("iframe#mainFrame");
            if (iframe != null) {
                String iframeSrc = iframe.attr("src");

                String fullIframeUrl = "https://blog.naver.com" + iframeSrc;

                Document iframeDocument = Jsoup.connect(fullIframeUrl).get();

                Elements imageElements = iframeDocument.select("div.se-module.se-module-image img");
                for (Element imgTag : imageElements) {
                    String imageUrl = imgTag.attr("data-lazy-src");
                    if (imageUrl.isEmpty()) {
                        imageUrl = imgTag.attr("src");
                    }
                    if (!imageUrl.isEmpty()) {
                        return imageUrl; // 첫 번째 이미지 URL 반환
                    }
                }
            } else {
                logger.warn("No iframe found in blog page: {}", blogUrl);
            }
        } catch (IOException e) {
            logger.error("Error fetching the blog page HTML", e);
            throw new TemplestayCoreException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return null;
    }

    public String getTempleNameById(Long templestayId) {
        Templestay templestay = templestayRepository.findById(templestayId)
                .orElseThrow(() -> new TemplestayNotFoundException());
        return templestay.getTempleName();
    }
}
