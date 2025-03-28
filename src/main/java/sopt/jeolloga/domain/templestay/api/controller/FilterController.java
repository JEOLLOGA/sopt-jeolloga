package sopt.jeolloga.domain.templestay.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.domain.templestay.api.dto.*;
import sopt.jeolloga.domain.templestay.api.service.FilterService;
import sopt.jeolloga.domain.templestay.api.service.TemplestaySearchService;

import java.util.List;

@RestController
public class FilterController {

    private final FilterService filterService;
    private final TemplestaySearchService templestaySearchService;

    public FilterController(FilterService filterService, TemplestaySearchService templestaySearchService) {

        this.filterService = filterService;
        this.templestaySearchService = templestaySearchService;
    }

    // 템플스테이 필터 반환
    @GetMapping("/public/filter")
    public ResponseEntity<FilterRes> getFilters() {

        FilterRes filterList = filterService.getFilters();
        return ResponseEntity.ok(filterList);
    }

    // 필터 초기화
    @GetMapping("/public/filter/reset")
    public ResponseEntity<ResetFilterRes> getResetFilter() {

        ResetFilterRes resetFilterRes = filterService.getFilterReset();
        return ResponseEntity.ok(resetFilterRes);
    }

    // 필터링 된 템플스테이 개수 반환
    @PostMapping("/public/filter/count")
    public ResponseEntity<FilterCountRes> getFilteredTemplestayNum(@RequestBody TemplestayFilterReqTemp filter) {

        List<Long> filteredId = filterService.getFiteredTemplestayCategory(filter);
        FilterCountRes filterCountRes = filterService.getFilteredTemplestayNum(filteredId);
        return ResponseEntity.ok(filterCountRes);
    }

    // 필터링 된 템플스테이 목록 반환
    @PostMapping("/filter/list")
    public ResponseEntity<PageTemplestayRes> getFilteredTemplestay(
            @RequestBody TemplestayFilterReqTemp filter,
            @RequestParam (value = "userId", required = false) Long userId,
            @RequestParam (value = "page") int page,
            @RequestParam (value="pageSize", defaultValue = "10") int pageSize,
            HttpServletRequest request){

        String accessToken = request.getHeader("Authorization");

        List<Long> filteredId;
        PageTemplestayRes templestayWithPage;

        filteredId = filterService.getFiteredTemplestayCategory(filter);

        if (accessToken != null && !accessToken.isEmpty()) {
            templestayWithPage = filterService.getFilteredTemplestay(filteredId, page, pageSize, userId);
        } else {
            userId = null;
            templestayWithPage = filterService.getFilteredTemplestay(filteredId, page, pageSize, userId);
        }
        return ResponseEntity.ok(templestayWithPage);
    }
}