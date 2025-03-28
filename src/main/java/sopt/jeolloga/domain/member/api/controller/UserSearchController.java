package sopt.jeolloga.domain.member.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.common.ResponseDto;
import sopt.jeolloga.domain.member.api.dto.DeleteSearchReq;
import sopt.jeolloga.domain.member.api.dto.MemberIdReq;
import sopt.jeolloga.domain.member.api.dto.SearchListRes;
import sopt.jeolloga.domain.member.api.service.SearchService;

@RequiredArgsConstructor
@RestController
public class UserSearchController {
    private final SearchService searchService;

    @GetMapping("/user/search/record")
    public ResponseEntity<SearchListRes> getSearchHistory(
            @RequestParam(value = "userId") Long userId
    ) {
        SearchListRes searchHistory = searchService.getSearchHistory(userId);
        return ResponseEntity.ok(searchHistory);
    }

    @DeleteMapping("/user/search/record/delete")
    public ResponseEntity<ResponseDto<Void>> deleteSearchRecord(
            @RequestBody DeleteSearchReq req
    ) {
        searchService.deleteSearchRecord(req.userId(), req.searchId());
        return ResponseEntity.ok(ResponseDto.success(null));
    }

    @DeleteMapping("/user/search/record/deleteAll")
    public ResponseEntity<ResponseDto<Void>> deleteAllSearchRecords(
            @RequestBody MemberIdReq memberIdReq) {
        searchService.deleteAllSearchRecords(memberIdReq.userId());
        return ResponseEntity.ok(ResponseDto.success(null));
    }
}
