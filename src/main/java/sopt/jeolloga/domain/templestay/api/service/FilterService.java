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

    public List<Map<String, Object>> getFiteredTemplestayCategory(Map<String, Object> filter) {

        this.filters = new Filters(filter);

        List<CategoryEntity> categoryEntityList = categoryRepository.findAll();
        List<Map<String, Object>> filteredCategory = filters.getFilteredCategory(categoryEntityList);

        return filteredCategory;
    }

    public FilterCountRes getFilteredTemplestayNum(List<Map<String, Object>> filteredCategory){

        FilterCountRes filterCountRes = new FilterCountRes(filteredCategory.size());
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



//    public List<TemplestayRes> getFilteredTemplestay(List<Map<String, Object>> filteredCategory) {
//
//        return filteredCategory.stream()
//                .map(this::getTemplestayRes) // 각 Map을 getTemplestayRes 호출
//                .collect(Collectors.toList()); // 결과 리스트로 변환
//    }

    public List<TemplestayRes> getFilteredTemplestay(List<Map<String, Object>> filteredCategory) {

        // id 값만 추출
        List<Long> ids = filteredCategory.stream()
                .map(entry -> (Long) entry.get("id"))
                .collect(Collectors.toList());

        // 조인된 데이터 조회
        List<Object[]> results = templestayRepository.findTemplestayWithImageUrls(ids);

        // 결과 매핑
        return results.stream()
                .map(result -> {
                    TemplestayEntity templestayEntity = (TemplestayEntity) result[0];
                    ImageUrlEntity imageUrlEntity = (ImageUrlEntity) result[1];

                    Long id = templestayEntity.getId();
                    String region = filteredCategory.stream()
                            .filter(entry -> id.equals(entry.get("id")))
                            .map(entry -> filters.getRegionFilterKey((Integer) entry.get("region")))
                            .findFirst()
                            .orElse(null);

                    String type = filteredCategory.stream()
                            .filter(entry -> id.equals(entry.get("id")))
                            .map(entry -> filters.getTypeFilterKey((Integer) entry.get("type")))
                            .findFirst()
                            .orElse(null);

                    String imgUrl = imageUrlEntity != null ? imageUrlEntity.getImgUrl() : null;

                    return new TemplestayRes(
                            id,
                            templestayEntity.getTempleName(),
                            templestayEntity.getTemplestayName(),
                            templestayEntity.getTag(),
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



//    // 특정 값으로 각 필터 value를 설정
//    private Map<String, Integer> convertListToMap(List<String> list, int value) {
//        Map<String, Integer> map = new HashMap<>();
//        for (String item : list) {
//            map.put(item, value);
//        }
//        return map;
//    }
}
