package sopt.jeolloga.domain.templestay.api.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sopt.jeolloga.domain.templestay.api.dto.*;
import sopt.jeolloga.domain.templestay.api.service.TemplestaySearchServiceV1;
import sopt.jeolloga.domain.templestay.api.service.TemplestaySearchServiceV2;

@RestController
public class TemplestaySearchTestController {

    private final TemplestaySearchServiceV1 searchServiceV1;
    private final TemplestaySearchServiceV2 searchServiceV2;

    public TemplestaySearchTestController(TemplestaySearchServiceV1 searchServiceV1, TemplestaySearchServiceV2 searchServiceV2) {
        this.searchServiceV1 = searchServiceV1;
        this.searchServiceV2 = searchServiceV2;
    }

    @PostMapping("public/search/v1")
    public ResponseEntity<PageTemplestaySearchRes<TemplestaySearchRes>> searchV1(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestBody TemplestayFilterReq templestayFilterReq,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        Pageable pageable = PageRequest.of(page - 1, pageSize);

        PageTemplestaySearchRes<TemplestaySearchRes> results = searchServiceV1.searchTemplestayWithFilters(
                userId,
                templestayFilterReq.content(),
                templestayFilterReq.region(),
                templestayFilterReq.type(),
                templestayFilterReq.purpose(),
                templestayFilterReq.activity(),
                templestayFilterReq.price().minPrice(),
                templestayFilterReq.price().maxPrice(),
                templestayFilterReq.etc(),
                pageable
        );
        return ResponseEntity.ok(results);
    }

    @PostMapping("public/search/v2")
    public ResponseEntity<PageTemplestaySearchRes<?>> searchV2(@RequestBody FilterReq filter,
                                                            @RequestParam (value = "userId", required = false) Long userId,
                                                            @RequestParam(value = "page", defaultValue = "1") int page,
                                                            @RequestParam (value="pageSize", defaultValue = "10") int pageSize){

        PageTemplestaySearchRes<TemplestayRes> result = searchServiceV2.searchFilteredTemplestay(filter, page, pageSize, userId);
        return ResponseEntity.ok(result);
    }
}
