package sopt.jeolloga.domain.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.common.ResponseDto;

@RequiredArgsConstructor
@RestController
public class UserSearchController {
    private final SearchService searchService;

    @GetMapping("/user/search/record")
    public ResponseEntity<SearchListRes> getSearchHistory(
            @RequestHeader(value = "userId") Long userId
    ) {
        SearchListRes searchHistory = searchService.getSearchHistory(userId);
        return ResponseEntity.ok(searchHistory);
    }

    @DeleteMapping("/user/search/record/delete")
    public ResponseEntity<ResponseDto<Void>> deleteSearchRecord(
            @RequestHeader(value = "userId") Long userId,
            @RequestBody DeleteSearchReq req
    ) {
        searchService.deleteSearchRecord(userId, req.searchId());
        return ResponseEntity.ok(ResponseDto.success(null));
    }

    @DeleteMapping("/user/search/record/deleteAll")
    public ResponseEntity<ResponseDto<Void>> deleteAllSearchRecords(
            @RequestHeader(value = "userId") Long userId) {
        searchService.deleteAllSearchRecords(userId);
        return ResponseEntity.ok(ResponseDto.success(null));
    }
}
