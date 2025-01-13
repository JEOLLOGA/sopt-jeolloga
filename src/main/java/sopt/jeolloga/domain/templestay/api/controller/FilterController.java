package sopt.jeolloga.domain.templestay.api.controller;

import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sopt.jeolloga.domain.templestay.api.dto.FilterRequestDto;
import sopt.jeolloga.domain.templestay.api.dto.FilterResponseDto;
import sopt.jeolloga.domain.templestay.api.service.FilterService;

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
    public ResponseEntity<Map<String, List<String>>> getFilters() {
        Map<String, List<String>> filters = filterService.getFilters();
        return ResponseEntity.ok(filters);
    }

}
