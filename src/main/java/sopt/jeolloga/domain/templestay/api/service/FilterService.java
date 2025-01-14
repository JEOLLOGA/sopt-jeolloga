package sopt.jeolloga.domain.templestay.api.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.stereotype.Service;
import sopt.jeolloga.common.Filters;
import sopt.jeolloga.domain.templestay.api.dto.FilterCountRes;
import sopt.jeolloga.domain.templestay.api.dto.FilterRes;
import sopt.jeolloga.domain.templestay.api.dto.ResetFilterRes;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayRes;
import sopt.jeolloga.domain.templestay.core.CategoryEntity;
import sopt.jeolloga.domain.templestay.core.CategoryRepository;
import sopt.jeolloga.domain.templestay.core.TemplestayEntity;
import sopt.jeolloga.domain.templestay.core.TemplestayRepository;

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
    private final TemplestayRepository templestaryRepository;

    public FilterService(Filters filters, CategoryRepository categoryRepository, TemplestayRepository templestaryRepository) {
        this.filters = filters;
        this.categoryRepository = categoryRepository;
        this.templestaryRepository = templestaryRepository;
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

//    public TemplestayRes getFiteredTemplestay(List<Long> ids) {
//
//        List<TemplestayEntity> templestayList = templestaryRepository.findByIdIn(ids);
//        List<CategoryEntity> categoryList = categoryRepository.findByIdIn(ids);
//
//        String region = filters.getRegionFilterKey();
//        String type = filters.getTypeFilterKey();
//
//        return templestaryRepository.findByIdIn(ids);
//    }

    // 초기화 상태의 필터를 반환
    public ResetFilterRes getFilterReset() {
        ResetFilterRes resetFilterRes = new ResetFilterRes(this.filters.getResetFilter(), this.templestaryRepository.count());
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
