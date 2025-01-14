package sopt.jeolloga.domain.templestay.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import sopt.jeolloga.domain.templestay.api.vo.NaverResultVO;
import sopt.jeolloga.domain.templestay.api.vo.TemplestayVO;
import sopt.jeolloga.domain.templestay.core.Review;
import sopt.jeolloga.domain.templestay.core.ReviewRepository;
import sopt.jeolloga.domain.templestay.core.TemplestayRepository;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewApiService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewApiService.class);
    private final TemplestayRepository templestayRepository;
    private final ReviewRepository reviewRepository;

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    public void saveBlogsToReviewTable() {
        logger.info("Fetching unique temple names from templestay table");

        List<String> distinctTempleNames = templestayRepository.findDistinctTempleNames();
        logger.info("Distinct temple names: {}", distinctTempleNames);

        distinctTempleNames.forEach(templeName -> {
            List<TemplestayVO> blogs = fetchBlogsFromNaverApi(templeName);
            blogs.stream()
                    .filter(blog -> blog.getPostdate().compareTo("20220101") > 0)
                    .forEach(blog -> saveReviewToRepository(templeName, blog));
        });
    }

    private void saveReviewToRepository(String templeName, TemplestayVO blog) {
        String truncatedDescription = truncateToLength(blog.getDescription(), 255);
        String truncatedTitle = truncateToLength(blog.getTitle(), 255);
        String truncatedBloggerName = truncateToLength(blog.getBloggername(), 45);
        String truncatedLink = truncateToLength(blog.getLink(), 500);

        // 필수 값 확인
        if (templeName == null || templeName.isEmpty()) {
            logger.warn("Temple name is missing. Skipping this entry.");
            return;
        }
        if (truncatedTitle == null || truncatedTitle.isEmpty()) {
            logger.warn("Title is missing for temple: {}. Skipping this entry.", templeName);
            return;
        }

        // Review 객체 생성 및 저장
        Review review = new Review(
                templeName,
                truncatedTitle,
                truncatedDescription,
                truncatedBloggerName,
                blog.getPostdate(),
                truncatedLink,
                null
        );
        reviewRepository.save(review);
        logger.info("Saved review to repository: {}", review);
    }


    // 길이 제한 메서드
    private String truncateToLength(String input, int maxLength) {
        if (input == null) {
            return null;
        }
        return input.length() > maxLength ? input.substring(0, maxLength) : input;
    }


    private String removeUnwantedCharacters(String input) {
        if (input == null) {
            return null;
        }
        // 유니코드 범위를 사용해 하트, 한자, 이모지 제거
        return input.replaceAll("[\\p{InCJKUnifiedIdeographs}\\p{So}]", "");
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

        logger.info("Response Status: {}", response.getStatusCode());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            NaverResultVO resultVO = objectMapper.readValue(response.getBody(), NaverResultVO.class);
            return resultVO.getItems();
        } catch (JsonProcessingException e) {
            logger.error("Error processing Naver API response", e);
            throw new RuntimeException("JSON Processing Error");
        }
    }
}

