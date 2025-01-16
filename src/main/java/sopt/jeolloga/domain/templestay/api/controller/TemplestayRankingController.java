package sopt.jeolloga.domain.templestay.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import sopt.jeolloga.common.ResponseDto;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayRankingListRes;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayRankingRes;
import sopt.jeolloga.domain.templestay.api.service.TemplestayRankingService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class TemplestayRankingController {
    private final TemplestayRankingService templestayRankingService;

    @GetMapping("/ranking")
    public ResponseEntity<TemplestayRankingListRes> getTopRanking(
            @RequestHeader(value = "userId", required = false) Long userId
    ) {
        TemplestayRankingListRes rankingList = templestayRankingService.getTopRankingList(userId);
        return ResponseEntity.ok(rankingList);
    }
}
