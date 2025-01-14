package sopt.jeolloga.domain.templestay.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sopt.jeolloga.domain.templestay.api.dto.FilterCountRes;
import sopt.jeolloga.domain.templestay.api.dto.FilterRes;
import sopt.jeolloga.domain.templestay.api.dto.ResetFilterRes;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayRes;
import sopt.jeolloga.domain.templestay.api.service.FilterService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FilterController {

    private final FilterService filterService;

    public FilterController(FilterService filterService) {
        this.filterService = filterService;
    }

    // 템플스테이 필터 반환
    @GetMapping("public/filter")
    public ResponseEntity<FilterRes> getFilters() {

        FilterRes filterList = filterService.getFilters();

        return ResponseEntity.ok(filterList);
    }

    // 필터 초기화
    @GetMapping("filter/reset")
    public ResponseEntity<ResetFilterRes> getResetFilter() {

        ResetFilterRes resetFilterRes = filterService.getFilterReset();

        return ResponseEntity.ok(resetFilterRes);
    }

    @GetMapping("public/filter/count")
    public ResponseEntity<FilterCountRes> getFilteredTemplestayNum(@RequestBody Map<String, Object> filter) {

        List<Map<String, Object>> filteredCategory = filterService.getFiteredTemplestayCategory(filter);
        FilterCountRes filterCountRes = filterService.getFilteredTemplestayNum(filteredCategory);

        return ResponseEntity.ok(filterCountRes);
    }

    @GetMapping("filter/list")
    public ResponseEntity<List<TemplestayRes>> getFilteredTemplestay(@RequestBody Map<String, Object> filter){

        List<Map<String, Object>> filteredCategory = filterService.getFiteredTemplestayCategory(filter);
        List<TemplestayRes> templestayList = filterService.getFilteredTemplestay(filteredCategory);

      return ResponseEntity.ok(templestayList);
    }
}
