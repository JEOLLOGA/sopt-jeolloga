package sopt.jeolloga.domain.templestay.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sopt.jeolloga.domain.templestay.api.dto.PageReviewRes;
import sopt.jeolloga.domain.templestay.api.dto.ReviewRes;
import sopt.jeolloga.domain.templestay.api.service.ReviewService;

@RequiredArgsConstructor
@RestController
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/public/templestay/{templestayId}/reviews")
    public PageReviewRes<ReviewRes> getPagedReviewsByTemplestayId(
            @PathVariable Long templestayId,
            @RequestParam("page") int page) {
        final int pageSize = 10;
        return reviewService.getPagedReviewsByTemplestayId(templestayId, page, pageSize);
    }
}