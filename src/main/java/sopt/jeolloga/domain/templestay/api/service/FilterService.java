package sopt.jeolloga.domain.templestay.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sopt.jeolloga.common.Filters;
import sopt.jeolloga.domain.templestay.api.dto.*;
import sopt.jeolloga.domain.templestay.core.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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


    public FilterRes getFilters() {
        FilterRes filterRes = new FilterRes(this.filters.getFilterKey());
        return filterRes;
    }


    public List<Long> getFiteredTemplestayCategory(Map<String, Object> filter) {

        this.filters = new Filters(filter);
        List<Category> categoryEntityList = categoryRepository.findAll();
        List<Long> filteredId = filters.getFilteredCategory(categoryEntityList);

        return filteredId;
    }


    public FilterCountRes getFilteredTemplestayNum(List<Long> filteredId){

        FilterCountRes filterCountRes = new FilterCountRes(filteredId.size());
        return filterCountRes;
    }


    public PageTemplesayRes getFilteredTemplestay(List<Long> ids, int page, int size) {


        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Object[]> resultsPage = templestayRepository.findTemplestayWithDetails(ids, pageable);

        Page<TemplestayRes> templestayResListPage = resultsPage.map(result -> {

            Long id = (Long) result[0];
            String templeName = Optional.ofNullable(result[1]).map(Object::toString).orElse("null");
            String organizedName = Optional.ofNullable(result[2]).map(Object::toString).orElse("null");
            String tags = Optional.ofNullable(result[3]).map(Object::toString).orElse("null");
            String tag = tags.split(",")[0];

            Integer binaryRegionFilter = result[4] != null ? ((Long) result[4]).intValue() : 0;
            Integer binaryTypeFilter = result[5] != null ? ((Long) result[5]).intValue() : 0;

            String region = (binaryRegionFilter == 0) ? "null" : filters.getFilterKey(binaryRegionFilter, filters.getRegionFilter());
            String type = (binaryTypeFilter == 0) ? "null" : filters.getFilterKey(binaryTypeFilter, filters.getTypeFilter());
            String imgUrl = Optional.ofNullable(result[6]).map(Object::toString).orElse("null");

            return new TemplestayRes(id, templeName, organizedName, tag, region, type, imgUrl);

        });

        return new PageTemplesayRes(templestayResListPage.getNumber() + 1, templestayResListPage.getSize(), templestayResListPage.getTotalPages(), templestayResListPage.getContent());
    }


    public ResetFilterRes getFilterReset() {
        ResetFilterRes resetFilterRes = new ResetFilterRes(this.filters.getResetFilter(), this.templestayRepository.count());
        return resetFilterRes;
    }



}
