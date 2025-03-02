package sopt.jeolloga.domain.templestay.api.controller;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.domain.member.core.exception.MemberCoreException;
import sopt.jeolloga.domain.templestay.api.dto.*;
import sopt.jeolloga.domain.templestay.api.service.FilterService;
import sopt.jeolloga.domain.templestay.api.service.FilterServiceV1;
import sopt.jeolloga.domain.templestay.api.service.FilterServiceV2;
import sopt.jeolloga.domain.templestay.api.service.TemplestaySearchService;
import sopt.jeolloga.exception.ErrorCode;

import java.util.List;

@RestController
public class FilterController {

    private final FilterService filterService;

    public FilterController(FilterService filterService,  FilterServiceV1 filterServiceV1, FilterServiceV2 filterServiceV2) {
        this.filterService = filterService;
    }

//    @GetMapping("/public/filter")
//    public ResponseEntity<FilterRes> getFilters() {
//
//        FilterRes filterList = filterService.getFilters();
//        return ResponseEntity.ok(filterList);
//    }

//    @GetMapping("/public/filter/reset")
//    public ResponseEntity<ResetFilterRes> getResetFilter() {
//
//        ResetFilterRes resetFilterRes = filterService.getFilterReset();
//        return ResponseEntity.ok(resetFilterRes);
//    }

    @PostMapping("public/filter/count")
    public ResponseEntity<FilterCountRes> getFilteredTemplestayNum(@RequestBody FilterReq filter){

        FilterCountRes filterCountRes = new FilterCountRes(filterService.getFilteredListNum(filter));
        return ResponseEntity.ok(filterCountRes);
    }

    @PostMapping("/filter/list")
    public ResponseEntity<PageTemplestayRes> getFilteredTemplestay(
            @RequestBody FilterReq filter,
            @RequestParam (value = "userId", required = false) Long userId,
            @RequestParam (value = "page") int page,
            @RequestParam (value="pageSize", defaultValue = "10") int pageSize){

        String authenticatedUser = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PageTemplestayRes templestayList;

        if(authenticatedUser == "anonymousUser"){
            templestayList = filterService.getTemplestayList(filter, page, pageSize, null);
        } else if (Long.parseLong(authenticatedUser) == userId) {
            templestayList = filterService.getTemplestayList(filter, page, pageSize, userId);
        } else {
            throw new MemberCoreException(ErrorCode.TOKEN_MISMATCH);
        }
        return ResponseEntity.ok(templestayList);
    }
}