package sopt.jeolloga.domain.templestay.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayDetailRes;
import sopt.jeolloga.domain.templestay.api.service.TemplestayService;

@RequiredArgsConstructor
@RestController
public class TemplestayController {
    private final TemplestayService templestayService;
    @GetMapping("/templestay/{templestayId}")
    public ResponseEntity<TemplestayDetailRes> getTemplestayDetails(
            @RequestHeader(value = "userId",required = false) Long userId,
            @PathVariable Long templestayId) {
        TemplestayDetailRes templestayDetailRes = templestayService.getTemplestayDetails(userId, templestayId);
        return ResponseEntity.ok(templestayDetailRes);
    }
}
