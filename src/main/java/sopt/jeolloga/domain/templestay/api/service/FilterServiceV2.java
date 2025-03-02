package sopt.jeolloga.domain.templestay.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sopt.jeolloga.common.FilterUtil;
import sopt.jeolloga.domain.templestay.api.dto.*;
import sopt.jeolloga.domain.templestay.core.TemplestayRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class FilterServiceV2 {

    private TemplestayRepository templestayRepository;

    public FilterServiceV2(TemplestayRepository templestayRepository) {
        this.templestayRepository = templestayRepository;
    }

    public long getFilteredListNum(FilterReq filter){

        String content = (filter.content() == null || filter.content().isBlank()) ? "" : filter.content().replaceAll("\\s+", "").trim();
        Integer binaryRegionFilter = FilterUtil.convertRegion(filter.region());
        Integer binaryTypeFilter = FilterUtil.convertType(filter.type());
        Integer binaryPurposeFilter = FilterUtil.convertPurpose(filter.purpose());
        Integer binaryActivityFilter = FilterUtil.convertActivity(filter.activity());
        Integer binaryEtcFilter = FilterUtil.convertEtc(filter.etc());

        int minPrice = filter.price().minPrice();
        int maxPrice = (filter.price().maxPrice() >= 300000) ? Integer.MAX_VALUE : filter.price().maxPrice();

        return templestayRepository.findFilteredTemplestayNum(content, binaryRegionFilter, binaryTypeFilter,
                binaryPurposeFilter,binaryActivityFilter, minPrice, maxPrice, binaryEtcFilter);
    }

    public PageTemplestayRes getTemplestayList(FilterReq filter, int page, int pageSize, Long userId){

        Pageable pageable = PageRequest.of(page-1, pageSize);

        Integer binaryRegionFilter = FilterUtil.convertRegion(filter.region());
        Integer binaryTypeFilter = FilterUtil.convertType(filter.type());
        Integer binaryPurposeFilter = FilterUtil.convertPurpose(filter.purpose());
        Integer binaryActivityFilter = FilterUtil.convertActivity(filter.activity());
        Integer binaryEtcFilter = FilterUtil.convertEtc(filter.etc());

        int minPrice = filter.price().minPrice();
        int maxPrice = (filter.price().maxPrice() >= 300000) ? Integer.MAX_VALUE : filter.price().maxPrice();

        Page<Object[]> filteredTemplestayPage = templestayRepository.findFilteredTemplestay(binaryRegionFilter, binaryTypeFilter,
                        binaryPurposeFilter,binaryActivityFilter, minPrice, maxPrice, binaryEtcFilter, userId, pageable);


        List<TemplestayRes> content = filteredTemplestayPage.getContent().stream()
                .map(row -> new TemplestayRes(
                        (Long) row[0],  // templestayId
                        (String) row[1],  // templeName
                        (String) row[2],  // organizedName
                        (String) row[3],  // tag
                        FilterUtil.convertRegionToString((Integer) row[4]),  // region
                        FilterUtil.convertTypeToString((Integer) row[5]),  // type
                        (String) row[6],  // imageUrl
                        ((Number) row[7]).intValue() == 1  // liked
                ))
                .collect(Collectors.toList());

        return new PageTemplestayRes(page, pageSize, filteredTemplestayPage.getTotalPages(), content);
    }
}
