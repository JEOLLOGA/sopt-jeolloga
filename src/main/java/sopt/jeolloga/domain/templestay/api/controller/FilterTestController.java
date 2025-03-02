package sopt.jeolloga.domain.templestay.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sopt.jeolloga.domain.templestay.api.dto.FilterCountRes;
import sopt.jeolloga.domain.templestay.api.dto.FilterReq;
import sopt.jeolloga.domain.templestay.api.dto.PageTemplestayRes;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayFilterReqTemp;
import sopt.jeolloga.domain.templestay.api.service.FilterServiceV1;
import sopt.jeolloga.domain.templestay.api.service.FilterServiceV2;

import java.util.List;

@RestController
public class FilterTestController {

    private final FilterServiceV1 filterServiceV1;
    private final FilterServiceV2 filterServiceV2;

    public FilterTestController(FilterServiceV1 filterServiceV1, FilterServiceV2 filterServiceV2) {
        this.filterServiceV1 = filterServiceV1;
        this.filterServiceV2 = filterServiceV2;
    }

    @PostMapping("/public/filter/list/v1")
    public ResponseEntity<PageTemplestayRes> filterListV1(
            @RequestBody TemplestayFilterReqTemp filter,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam (value = "page") int page,
            @RequestParam (value="pageSize", defaultValue = "10") int pageSize){

        List<Long> filteredId;
        PageTemplestayRes templestayWithPage;

        filteredId = filterServiceV1.getFiteredTemplestayCategory(filter);
        templestayWithPage = filterServiceV1.getFilteredTemplestay(filteredId, page, pageSize, userId);

        return ResponseEntity.ok(templestayWithPage);
    }

    @PostMapping("public/filter/list/v2")
    public ResponseEntity<PageTemplestayRes> filterListV2(
            @RequestBody FilterReq filter,
            @RequestParam (value = "userId", required = false) Long userId,
            @RequestParam (value = "page") int page,
            @RequestParam (value="pageSize", defaultValue = "10") int pageSize){

        PageTemplestayRes result = filterServiceV2.getTemplestayList(filter, page, pageSize, userId);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/public/filter/count/v1")
    public ResponseEntity<FilterCountRes> filterCountV1(@RequestBody TemplestayFilterReqTemp filter) {

        List<Long> filteredId = filterServiceV1.getFiteredTemplestayCategory(filter);
        FilterCountRes filterCountRes = filterServiceV1.getFilteredTemplestayNum(filteredId);
        return ResponseEntity.ok(filterCountRes);
    }

    @PostMapping("public/filter/count/v2")
    public ResponseEntity<FilterCountRes> filterCountV2(@RequestBody FilterReq filter){

        FilterCountRes filterCountRes = new FilterCountRes(filterServiceV2.getFilteredListNum(filter));

        return ResponseEntity.ok(filterCountRes);
    }
}
