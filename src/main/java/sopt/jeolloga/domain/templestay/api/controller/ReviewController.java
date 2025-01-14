package sopt.jeolloga.domain.templestay.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.domain.templestay.api.service.ReviewService;
import sopt.jeolloga.domain.templestay.api.vo.TemplestayVO;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/public/templestay")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Templestay ID를 기반으로 블로그 정보를 가져오는 API
     *
     * @param templestayId 템플스테이 ID
     * @return 블로그 정보 리스트
     */
    @GetMapping("/{templestayId}/reviews")
    public ResponseEntity<List<TemplestayVO>> getBlogsByTemplestayId(@PathVariable Long templestayId) {
        List<TemplestayVO> blogs = reviewService.getBlogsByTemplestayId(templestayId);
        return ResponseEntity.ok(blogs);
    }
}
