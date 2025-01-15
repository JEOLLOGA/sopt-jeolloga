package sopt.jeolloga.domain.templestay.api.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import sopt.jeolloga.common.ResponseDto;
import sopt.jeolloga.domain.templestay.api.dto.ReviewRes;
import sopt.jeolloga.domain.templestay.api.service.ReviewService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/public/templestay/{templestayId}/reviews")
    public ResponseEntity<?> getReviewsByTemplestayID(@PathVariable Long templestayId) {
        List<ReviewRes> reviews = reviewService.getReviewsByTemplestayId(templestayId);
        return ResponseEntity.ok(Map.of("reviews", reviews));
    }
}