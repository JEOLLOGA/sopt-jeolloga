package sopt.jeolloga.domain.templestay.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.domain.templestay.api.dto.TemplestaySearchRes;
import sopt.jeolloga.domain.templestay.api.service.TemplestaySearchService;
import sopt.jeolloga.domain.wishlist.api.dto.PageWishlistRes;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class TemplestaySearchController {
    private final TemplestaySearchService templestaySearchService;

    @PostMapping("/search")
    public ResponseEntity<PageWishlistRes<TemplestaySearchRes>> search(
            @RequestHeader(value = "userId", required = false) Long userId,
            @RequestBody String query,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
    ) {
        PageWishlistRes<TemplestaySearchRes> results = templestaySearchService.searchTemplestay(userId, query, page, pageSize);
        return ResponseEntity.ok(results);
    }

}

