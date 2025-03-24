package sopt.jeolloga.domain.templestay.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.domain.member.core.exception.MemberCoreException;
import sopt.jeolloga.domain.templestay.api.dto.*;
import sopt.jeolloga.domain.templestay.api.service.TemplestaySearchService;
import sopt.jeolloga.exception.ErrorCode;

@RestController
public class TemplestaySearchController {
    private final TemplestaySearchService templestaySearchService;

    public TemplestaySearchController(TemplestaySearchService templestaySearchService){
        this.templestaySearchService = templestaySearchService;
    }

    @PostMapping("/search")
    public ResponseEntity<PageTemplestaySearchRes<?>> searchWithFilters(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestBody FilterReq filter,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        String authenticatedUser = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PageTemplestaySearchRes<TemplestayRes> templestaySearchRes;

        if("anonymousUser".equals(authenticatedUser)){
            templestaySearchRes = templestaySearchService.searchFilteredTemplestay(filter, page, pageSize, null);
        } else if (Long.parseLong(authenticatedUser) == userId) {
            templestaySearchRes = templestaySearchService.searchFilteredTemplestay(filter, page, pageSize, userId);
        } else {
            throw new MemberCoreException(ErrorCode.TOKEN_MISMATCH);
        }

        return ResponseEntity.ok(templestaySearchRes);
    }
}