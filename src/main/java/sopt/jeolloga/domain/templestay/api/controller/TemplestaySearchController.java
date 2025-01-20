package sopt.jeolloga.domain.templestay.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.domain.templestay.api.dto.PageTemplestaySearchRes;
import sopt.jeolloga.domain.templestay.api.dto.TemplestaySearchReq;
import sopt.jeolloga.domain.templestay.api.dto.TemplestaySearchRes;
import sopt.jeolloga.domain.templestay.api.service.TemplestaySearchService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class TemplestaySearchController {
    private final TemplestaySearchService templestaySearchService;

    @PostMapping("/search")
    public ResponseEntity<PageTemplestaySearchRes<TemplestaySearchRes>> search(
            @RequestBody TemplestaySearchReq templestaySearchReq,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Long userId = templestaySearchReq.userId();
        String query = templestaySearchReq.content();
        PageTemplestaySearchRes<TemplestaySearchRes> results = templestaySearchService.searchTemplestay(userId, query, page, pageSize);
        return ResponseEntity.ok(results);
    }
}
