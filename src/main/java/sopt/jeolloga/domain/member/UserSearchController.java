package sopt.jeolloga.domain.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserSearchController {
    private final SearchService searchService;

    @GetMapping("/user/search/record")
    public ResponseEntity<SearchListRes> getSearchHistory(
            @RequestHeader(value = "userId") Long userId
    ) {
        // 인스턴스를 통해 메서드 호출
        SearchListRes searchHistory = searchService.getSearchHistory(userId);
        return ResponseEntity.ok(searchHistory);
    }
}
