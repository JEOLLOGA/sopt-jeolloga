package sopt.jeolloga.domain.templestay.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.domain.templestay.api.service.ReviewService;
import sopt.jeolloga.domain.templestay.api.vo.TemplestayVO;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;
    @GetMapping("/public/templestay/{templestayId}/reviews")
    public ResponseEntity<List<TemplestayVO>> getBlogsByTemplestayId(@PathVariable Long templestayId) {
        List<TemplestayVO> blogs = reviewService.getBlogsByTemplestayId(templestayId);
        return ResponseEntity.ok(blogs);
    }
}
