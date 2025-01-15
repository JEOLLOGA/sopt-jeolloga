package sopt.jeolloga.domain.templestay.api.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sopt.jeolloga.common.Filters;
import sopt.jeolloga.domain.templestay.api.dto.FilterCountRes;
import sopt.jeolloga.domain.templestay.api.dto.FilterRes;
import sopt.jeolloga.domain.templestay.api.dto.ResetFilterRes;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayRes;
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
    private final ImageUrlRepository imageUrlRepository;

    public FilterService(Filters filters, CategoryRepository categoryRepository, TemplestayRepository templestaryRepository, ImageUrlRepository imageUrlRepository) {
        this.filters = filters;
        this.categoryRepository = categoryRepository;
        this.templestayRepository = templestaryRepository;
        this.imageUrlRepository = imageUrlRepository;
    }

    public FilterRes getFilters() {
        FilterRes filterRes = new FilterRes(this.filters.getFilterKey());
        return filterRes;
    }

    public List<Long> getFiteredTemplestayCategory(Map<String, Object> filter) {

        this.filters = new Filters(filter);
        List<CategoryEntity> categoryEntityList = categoryRepository.findAll();
        List<Long> filteredId = filters.getFilteredCategory(categoryEntityList);

        return filteredId;
    }

    public FilterCountRes getFilteredTemplestayNum(List<Long> filteredId){

        FilterCountRes filterCountRes = new FilterCountRes(filteredId.size());
        return filterCountRes;
    }

    public List<Long> categoryToId(List<Map<String, Object>> filteredCategory) {

        return filteredCategory.stream()
                .map(entry -> Long.valueOf(entry.get("id").toString()))
                .collect(Collectors.toList());
    }

    public TemplestayRes getTemplestayRes(Map<String, Object> category){

        Long id = (Long)category.get("id");
        String region = filters.getRegionFilterKey((Integer)category.get("region"));
        String type = filters.getTypeFilterKey((Integer)category.get("type"));

        TemplestayEntity templestayEntity = templestayRepository.findById(id).orElse(null);
        ImageUrlEntity imageUrlEntity = imageUrlRepository.findById(id).orElse(null);

        TemplestayRes templestayRes = new TemplestayRes(id,templestayEntity.getTempleName(), templestayEntity.getTemplestayName(), templestayEntity.getTag(), region, type, imageUrlEntity.getImgUrl());

        return templestayRes;
    }


    public List<TemplestayRes> getFilteredTemplestay(List<Long> ids) {

        // 조인된 데이터 조회
        List<Object[]> results = templestayRepository.findTemplestayWithDetails(ids);

//        for (Object[] row : results) {
//            // 각 배열 원소의 값을 로그로 출력하여 확인
//            for (int i = 0; i < row.length; i++) {
//                System.out.println("Index " + i + ": " + row[i]);
//                Object obj = row[i];
//                System.out.println(obj.getClass().getName());
//            }
//        }

        // 결과 매핑
        return results.stream()
                .map(result -> {

                    // 필터링 처리 (필요한 값 추출)
                    Long id = (Long) result[0];
                    String templeName = Optional.ofNullable(result[1]).map(Object::toString).orElse("null");
                    String templestayName = Optional.ofNullable(result[2]).map(Object::toString).orElse("null");
                    String tag = Optional.ofNullable(result[3]).map(Object::toString).orElse("null");

                    Integer binaryRegionFilter = result[4] != null ? ((Long) result[4]).intValue() : 0;
                    Integer binarTypeFilter = result[5] != null ? ((Long) result[5]).intValue() : 0;

                    String region = "";
                    if(binaryRegionFilter == 0){
                        region = "null";
                    } else {
                        region = filters.getRegionFilterKey(binaryRegionFilter);
                    }

                    String type = "";
                    if(binarTypeFilter == 0){
                        type = "null";
                    } else {
                        type = filters.getTypeFilterKey(binarTypeFilter);
                    }

                    String imgUrl = Optional.ofNullable(result[6]).map(Object::toString).orElse("null");

                    // TemplestayRes 객체 생성
                    return new TemplestayRes(
                            id,
                            templeName,
                            templestayName,
                            tag,
                            region,
                            type,
                            imgUrl
                    );
                })
                .collect(Collectors.toList());
    }

    // 초기화 상태의 필터를 반환
    public ResetFilterRes getFilterReset() {
        ResetFilterRes resetFilterRes = new ResetFilterRes(this.filters.getResetFilter(), this.templestayRepository.count());
        return resetFilterRes;
    }

}
