package sopt.jeolloga.domain.templestay.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayViewReq;
import sopt.jeolloga.domain.templestay.api.service.TemplestayViewService;

@RequiredArgsConstructor
@RestController
public class TemplestayViewController {

    private final TemplestayViewService templestayViewService;

    @PostMapping("/public/view")
    public ResponseEntity<?> searchWithFilters(TemplestayViewReq request){
        templestayViewService.addView(request);
        return ResponseEntity.ok().build();
    }

}
