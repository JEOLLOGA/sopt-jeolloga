package sopt.jeolloga.domain.templestay.api.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.common.ResponseDto;
import sopt.jeolloga.domain.templestay.api.service.ReviewApiService;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewApiController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewApiController.class);
    private final ReviewApiService reviewApiService;

    @PostMapping("/fetch-and-save")
    public ResponseEntity<ResponseDto<String>> fetchAndSaveReviews() {
        try {
            logger.info("Starting to fetch and save blogs into the review table");
            reviewApiService.saveBlogsToReviewTable();
            return ResponseEntity.ok(ResponseDto.success("블로그 데이터를 성공적으로 저장했습니다."));
        } catch (Exception e) {
            logger.error("Error occurred while fetching and saving blogs", e);
            return ResponseEntity
                    .status(500)
                    .body(ResponseDto.fail(500, "블로그 데이터를 저장하는 중 오류가 발생했습니다."));
        }
    }
}