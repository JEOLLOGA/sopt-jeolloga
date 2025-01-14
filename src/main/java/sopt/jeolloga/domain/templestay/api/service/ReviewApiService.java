package sopt.jeolloga.domain.templestay.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import sopt.jeolloga.common.ResponseDto;
import sopt.jeolloga.domain.templestay.api.vo.NaverResultVO;
import sopt.jeolloga.domain.templestay.api.vo.TemplestayVO;
import sopt.jeolloga.domain.templestay.core.Review;
import sopt.jeolloga.domain.templestay.core.ReviewRepository;
import sopt.jeolloga.domain.templestay.core.TemplestayRepository;
import sopt.jeolloga.domain.templestay.core.exception.TemplestayCoreException;
import sopt.jeolloga.exception.ErrorCode;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ReviewApiService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewApiService.class);
    private final TemplestayRepository templestayRepository;
    private final ReviewRepository reviewRepository;

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    public ResponseDto<?> saveBlogsToReviewTable() {
        try {
            List<String> distinctTempleNames = templestayRepository.findDistinctTempleNames();

            if (distinctTempleNames.isEmpty()) {
                logger.warn("No distinct temple names found in TemplestayRepository.");
                throw new TemplestayCoreException(ErrorCode.NOT_FOUND_TEMPLESTAY);
            }

            distinctTempleNames.forEach(templeName -> {
                try {
                    List<TemplestayVO> blogs = fetchBlogsFromNaverApi(templeName);
                    blogs.stream()
                            .filter(blog -> blog.getPostdate().compareTo("20220101") > 0)
                            .forEach(blog -> {
                                try {
                                    saveReviewToRepository(templeName, blog);
                                } catch (TemplestayCoreException e) {
                                    logger.warn("Skipping blog due to validation error: {}", e.getErrorCode().getMsg());
                                }
                            });
                } catch (TemplestayCoreException e) {
                    logger.error("Failed to fetch blogs for temple: {} - {}", templeName, e.getErrorCode().getMsg());
                }
            });

            return ResponseDto.success("Reviews successfully saved.");
        } catch (TemplestayCoreException e) {
            logger.error("TemplestayCoreException: {}", e.getErrorCode().getMsg());
            return ResponseDto.fail(e.getErrorCode().getMsg());
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage());
            return ResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR.getMsg());
        }
    }

    private void saveReviewToRepository(String templeName, TemplestayVO blog) {
        if (templeName == null || templeName.isEmpty()) {
            throw new TemplestayCoreException(ErrorCode.MISSING_TEMPLE_NAME);
        }

        String truncatedTitle = truncateToLength(blog.getTitle(), 255);
        if (truncatedTitle == null || truncatedTitle.isEmpty()) {
            throw new TemplestayCoreException(ErrorCode.MISSING_TITLE);
        }

        String truncatedDescription = truncateToLength(blog.getDescription(), 255);
        String truncatedBloggerName = truncateToLength(blog.getBloggername(), 45);
        String truncatedLink = truncateToLength(blog.getLink(), 500);

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
    }

    private List<TemplestayVO> fetchBlogsFromNaverApi(String templeName) {
        try {
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

            RequestEntity<Void> requestEntity = RequestEntity
                    .get(uri)
                    .header("X-Naver-Client-Id", clientId)
                    .header("X-Naver-Client-Secret", clientSecret)
                    .build();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new TemplestayCoreException(ErrorCode.API_CALL_FAILED);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.getBody(), NaverResultVO.class).getItems();
        } catch (JsonProcessingException e) {
            logger.error("JSON processing error: {}", e.getMessage());
            throw new TemplestayCoreException(ErrorCode.JSON_FIELD_ERROR);
        } catch (Exception e) {
            logger.error("Error fetching blogs from Naver API: {}", e.getMessage());
            throw new TemplestayCoreException(ErrorCode.API_CALL_FAILED);
        }
    }

    private String truncateToLength(String input, int maxLength) {
        return (input == null) ? null : (input.length() > maxLength ? input.substring(0, maxLength) : input);
    }
}
