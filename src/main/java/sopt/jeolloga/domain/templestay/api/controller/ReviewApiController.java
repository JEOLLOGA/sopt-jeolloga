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

    private final ReviewApiService reviewApiService;

    @PostMapping("/fetch-and-save")
    public ResponseEntity<ResponseDto<String>> fetchAndSaveReviews() {
        try {
            reviewApiService.saveBlogsToReviewTable();
            return ResponseEntity.ok(ResponseDto.success("블로그 데이터를 성공적으로 저장했습니다."));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ResponseDto.fail(500, "블로그 데이터를 저장하는 중 오류가 발생했습니다."));
        }
    }
}