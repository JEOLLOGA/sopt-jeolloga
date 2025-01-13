package sopt.jeolloga;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    private final TemplestayRepository templestayRepository;

    public List<TemplestayVO> getBlogsByTemplestayId(Long templestayId) {
        logger.info("Fetching templestay reviews for ID: {}", templestayId);

        TemplestayEntity templestayEntity = templestayRepository.findById(templestayId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid templestay ID: " + templestayId));

        String templeName = templestayEntity.getTempleName();
        logger.info("Temple name: {}", templeName);

        return fetchBlogsFromNaverApi(templeName);
    }

    private List<TemplestayVO> fetchBlogsFromNaverApi(String templeName) {
        logger.info("Fetching blogs from Naver API for temple name: {}", templeName);

        String clientId = "kwWkZv4rtI3mEdO04fAR";
        String clientSecret = "yBTo19o1kx";

        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/blog.json")
                .queryParam("query", templeName + " 템플스테이")
                .queryParam("display", 10)
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

        logger.info("Response status: {}", response.getStatusCode());
        logger.info("Response body: {}", response.getBody());

        ObjectMapper objectMapper = new ObjectMapper();
        NaverResultVO resultVO;

        try {
            resultVO = objectMapper.readValue(response.getBody(), NaverResultVO.class);
        } catch (JsonProcessingException e) {
            logger.error("Error while processing Naver API response", e);
            throw new RuntimeException("Error while processing Naver API response", e);
        }

        for (TemplestayVO item : resultVO.getItems()) {
            item.setThumbnail(fetchFirstImageFromBlog(item.getLink()));
        }

        return resultVO.getItems();
    }

    private String fetchFirstImageFromBlog(String blogUrl) {
        try {
            logger.info("Connecting to blog URL: {}", blogUrl);

            Document document = Jsoup.connect(blogUrl).get();

            Element iframe = document.selectFirst("iframe#mainFrame");
            if (iframe != null) {
                String iframeSrc = iframe.attr("src");

                String fullIframeUrl = "https://blog.naver.com" + iframeSrc;
                logger.info("Extracting iframe URL: {}", fullIframeUrl);

                Document iframeDocument = Jsoup.connect(fullIframeUrl).get();

                Elements imageElements = iframeDocument.select("div.se-module.se-module-image img");
                for (Element imgTag : imageElements) {
                    String imageUrl = imgTag.attr("data-lazy-src");
                    if (imageUrl.isEmpty()) {
                        imageUrl = imgTag.attr("src");
                    }
                    if (!imageUrl.isEmpty()) {
                        logger.info("Extracted Image URL: {}", imageUrl);
                        return imageUrl; // 첫 번째 이미지 URL 반환
                    }
                }
            } else {
                logger.warn("No iframe found in blog page: {}", blogUrl);
            }
        } catch (IOException e) {
            logger.error("Error fetching the blog page HTML", e);
        }

        return null;
    }


    private String resolveRedirectedUrl(String blogUrl) {
        try {
            return Jsoup.connect(blogUrl)
                    .followRedirects(true)
                    .execute()
                    .url()
                    .toString(); // 리디렉션된 URL 반환
        } catch (IOException e) {
            logger.error("Error resolving redirected URL", e);
        }
        return blogUrl;
    }
}
