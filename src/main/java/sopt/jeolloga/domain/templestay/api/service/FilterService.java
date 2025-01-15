package sopt.jeolloga.domain.templestay.api.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sopt.jeolloga.common.Filters;
import sopt.jeolloga.domain.templestay.api.dto.*;
import sopt.jeolloga.domain.templestay.core.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Filter;
import java.util.stream.Collectors;

@Service
public class FilterService {

    private Filters filters;
    private final CategoryRepository categoryRepository;
    private final TemplestayRepository templestayRepository;

    public FilterService(Filters filters, CategoryRepository categoryRepository, TemplestayRepository templestaryRepository, ImageUrlRepository imageUrlRepository) {
        this.filters = filters;
        this.categoryRepository = categoryRepository;
        this.templestayRepository = templestaryRepository;
    }

    // 사용 중인 필터 목록 반환
    public FilterRes getFilters() {
        FilterRes filterRes = new FilterRes(this.filters.getFilterKey());
        return filterRes;
    }

    // 필터에 의해 걸러진 id 리스트 반환
    public List<Long> getFiteredTemplestayCategory(Map<String, Object> filter) {

        this.filters = new Filters(filter);
        List<CategoryEntity> categoryEntityList = categoryRepository.findAll();
        List<Long> filteredId = filters.getFilteredCategory(categoryEntityList);

        return filteredId;
    }

    // 필터에 의해 걸러진 id 개수 반환
    public FilterCountRes getFilteredTemplestayNum(List<Long> filteredId){

        FilterCountRes filterCountRes = new FilterCountRes(filteredId.size());
        return filterCountRes;
    }

    // 필터에 의해 걸러진 템플스테이 리스트 반환
    public PagingRes getFilteredTemplestay(List<Long> ids, int page, int size) {

        // Paging 처리
        Pageable pageable = PageRequest.of(page, size);

        // 조인된 데이터 조회
        Page<Object[]> resultsPage = templestayRepository.findTemplestayWithDetails(ids, pageable);

        Page<TemplestayRes> templestayResListPage = resultsPage.map(result -> {

            Long id = (Long) result[0];
            String templeName = Optional.ofNullable(result[1]).map(Object::toString).orElse("null");
            String organizedName = Optional.ofNullable(result[2]).map(Object::toString).orElse("null");
            String tags = Optional.ofNullable(result[3]).map(Object::toString).orElse("null");
            String tag = tags.split(",")[0];

            Integer binaryRegionFilter = result[4] != null ? ((Long) result[4]).intValue() : 0;
            Integer binaryTypeFilter = result[5] != null ? ((Long) result[5]).intValue() : 0;

            String region = (binaryRegionFilter == 0) ? "null" : filters.getRegionFilterKey(binaryRegionFilter);
            String type = (binaryTypeFilter == 0) ? "null" : filters.getTypeFilterKey(binaryTypeFilter);
            String imgUrl = Optional.ofNullable(result[6]).map(Object::toString).orElse("null");

            return new TemplestayRes(id, templeName, organizedName, tag, region, type, imgUrl);

        });

        return new PagingRes(templestayResListPage.getNumber() + 1, templestayResListPage.getSize(), templestayResListPage.getTotalPages(), templestayResListPage.getContent());
    }

    // 초기화 상태의 필터를 반환
    public ResetFilterRes getFilterReset() {
        ResetFilterRes resetFilterRes = new ResetFilterRes(this.filters.getResetFilter(), this.templestayRepository.count());
        return resetFilterRes;
    }

}
