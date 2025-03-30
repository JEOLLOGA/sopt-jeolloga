package sopt.jeolloga.domain.member.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sopt.jeolloga.domain.member.api.dto.RecentViewReq;
import sopt.jeolloga.domain.member.api.dto.RecentViewRes;
import sopt.jeolloga.domain.member.api.service.RecentViewService;
import sopt.jeolloga.domain.member.core.exception.MemberCoreException;
import sopt.jeolloga.exception.ErrorCode;

@RequiredArgsConstructor
@RestController
public class RecentViewController {

    private final RecentViewService recentViewService;

    @PostMapping("/user/templestay/recent")
    public ResponseEntity<?> saveRecentView(RecentViewReq request){

        String authenticatedUser = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long memberId = request.memberId();

        if("anonymousUser".equals(authenticatedUser)){
            throw new MemberCoreException(ErrorCode.MISSING_ACCESS_TOKEN);
        } else if (Long.parseLong(authenticatedUser) == memberId) {
            recentViewService.saveRecentView(request);
        } else {
            throw new MemberCoreException(ErrorCode.TOKEN_MISMATCH);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/templestay/recent")
    public ResponseEntity<RecentViewRes> getRecentView(
            @RequestParam(value = "userId") Long memberId
    ){

        String authenticatedUser = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RecentViewRes response;

        if("anonymousUser".equals(authenticatedUser)){
            throw new MemberCoreException(ErrorCode.MISSING_ACCESS_TOKEN);
        } else if (Long.parseLong(authenticatedUser) == memberId) {
             response = recentViewService.getRecentViewedTemplestays(memberId);
        } else {
            throw new MemberCoreException(ErrorCode.TOKEN_MISMATCH);
        }

        return ResponseEntity.ok(response);
    }

}
