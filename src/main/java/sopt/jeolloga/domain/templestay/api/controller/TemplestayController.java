package sopt.jeolloga.domain.templestay.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayDetailRes;
import sopt.jeolloga.domain.templestay.api.service.TemplestayService;

@RequiredArgsConstructor
@RestController
public class TemplestayController {
    private final TemplestayService templestayService;
    @GetMapping("/templestay")
    public ResponseEntity<TemplestayDetailRes> getTemplestayDetails(
            @RequestParam(value = "userId",required = false) Long userId,
            @RequestParam Long templestayId) {
        TemplestayDetailRes templestayDetailRes = templestayService.getTemplestayDetails(userId, templestayId);
        return ResponseEntity.ok(templestayDetailRes);
    }
}
