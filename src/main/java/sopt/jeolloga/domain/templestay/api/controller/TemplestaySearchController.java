package sopt.jeolloga.domain.templestay.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.domain.templestay.api.dto.PageTemplestaySearchRes;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayFilterReq;
import sopt.jeolloga.domain.templestay.api.dto.TemplestaySearchRes;
import sopt.jeolloga.domain.templestay.api.service.TemplestaySearchService;

import java.util.Optional;
import java.util.OptionalLong;

@RequiredArgsConstructor
@RestController
public class TemplestaySearchController {
    private final TemplestaySearchService templestaySearchService;

    @PostMapping("/search")
    public ResponseEntity<PageTemplestaySearchRes<TemplestaySearchRes>> searchWithFilters(
            @RequestParam(value = "userId", required = false) Long userId, // required = false로 설정
            @RequestBody TemplestayFilterReq templestayFilterReq,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        Pageable pageable = PageRequest.of(page - 1, pageSize);

        PageTemplestaySearchRes<TemplestaySearchRes> results = templestaySearchService.searchTemplestayWithFilters(
                userId, // userId가 null일 수도 있음
                templestayFilterReq.content(),
                templestayFilterReq.region(),
                templestayFilterReq.type(),
                templestayFilterReq.purpose(),
                templestayFilterReq.activity(),
                templestayFilterReq.etc(),
                pageable
        );

        return ResponseEntity.ok(results);
    }
}