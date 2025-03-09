package sopt.jeolloga.domain.templestay.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.domain.member.core.exception.MemberCoreException;
import sopt.jeolloga.domain.templestay.api.dto.*;
import sopt.jeolloga.domain.templestay.api.service.FilterService;
import sopt.jeolloga.exception.ErrorCode;

@RestController
public class FilterController {

    private final FilterService filterService;

    public FilterController(FilterService filterService) {
        this.filterService = filterService;
    }

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