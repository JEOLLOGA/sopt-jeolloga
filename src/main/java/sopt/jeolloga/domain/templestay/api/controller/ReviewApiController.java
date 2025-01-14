package sopt.jeolloga.domain.templestay.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.common.ResponseDto;
import sopt.jeolloga.domain.templestay.api.service.ReviewApiService;
import sopt.jeolloga.domain.templestay.api.service.ReviewImgUrlCrawling;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewApiController {

    private final ReviewApiService reviewApiService;
    private final ReviewImgUrlCrawling reviewImgUrlCrawling;

    @PostMapping("/fetch-and-save")
    public ResponseEntity<ResponseDto<String>> fetchAndSaveReviews() {
        reviewApiService.saveBlogsToReviewTable();
        return ResponseEntity.ok(ResponseDto.success("블로그 데이터를 성공적으로 저장했습니다."));
    }

    @PostMapping("/fetch-and-save-images")
    public ResponseEntity<ResponseDto<String>> fetchAndSaveAllReviewImages() {
        reviewImgUrlCrawling.fetchAndSaveAllReviewImages();
        return ResponseEntity.ok(ResponseDto.success("모든 리뷰의 이미지 URL을 성공적으로 저장했습니다."));
    }

}
