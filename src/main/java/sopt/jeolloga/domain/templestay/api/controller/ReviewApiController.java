package sopt.jeolloga.domain.templestay.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.common.ResponseDto;
import sopt.jeolloga.domain.templestay.api.service.ReviewApiService;

@RestController
@RequiredArgsConstructor
public class ReviewApiController {

    private final ReviewApiService reviewApiService;

    @PostMapping("/api/reviews/fetch-and-save")
    public ResponseEntity<ResponseDto<String>> fetchAndSaveReviews() {
        reviewApiService.saveBlogsToReviewTable();
        return ResponseEntity.ok(ResponseDto.success("블로그 데이터를 성공적으로 저장했습니다."));
    }
}
