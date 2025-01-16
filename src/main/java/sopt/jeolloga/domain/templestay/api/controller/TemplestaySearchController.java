package sopt.jeolloga.domain.templestay.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.domain.templestay.api.dto.TemplestaySearchRes;
import sopt.jeolloga.domain.templestay.api.service.TemplestaySearchService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class TemplestaySearchController {
    private final TemplestaySearchService templestaySearchService;

    @PostMapping("/search")
    public ResponseEntity<List<TemplestaySearchRes>> search(
            @RequestHeader(value = "userId", required = false) Long userId,
            @RequestBody String query
    ) {
        List<TemplestaySearchRes> results = templestaySearchService.searchTemplestay(userId, query);
        return ResponseEntity.ok(results);
    }
}

