package sopt.jeolloga.domain.templestay.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayImgRes;
import sopt.jeolloga.domain.templestay.api.service.TemplestayImageService;

@RequiredArgsConstructor
@RestController
public class TemplestayImageController {
    private final TemplestayImageService templestayImageService;

    @GetMapping("/public/templestay/img")
    public ResponseEntity<TemplestayImgRes> getTemplestayImages(
            @RequestParam Long templestayId) {
        TemplestayImgRes templestayImgRes = templestayImageService.getTemplestayImages(templestayId);
        return ResponseEntity.ok(templestayImgRes);
    }
}