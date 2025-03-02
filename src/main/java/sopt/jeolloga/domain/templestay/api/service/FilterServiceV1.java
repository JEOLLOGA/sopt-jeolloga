package sopt.jeolloga.domain.templestay.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sopt.jeolloga.common.Filters;
import sopt.jeolloga.domain.templestay.api.dto.*;
import sopt.jeolloga.domain.templestay.core.*;
import sopt.jeolloga.domain.wishlist.core.WishlistRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FilterServiceV1 {


    private Filters filters;
    private final CategoryRepository categoryRepository;
    private final TemplestayRepository templestayRepository;
    private final WishlistRepository wishlistRepository;


    public FilterServiceV1(Filters filters, CategoryRepository categoryRepository, TemplestayRepository templestaryRepository, WishlistRepository wishlistRepository) {
        this.filters = filters;
        this.categoryRepository = categoryRepository;
        this.templestayRepository = templestaryRepository;
        this.wishlistRepository = wishlistRepository;
    }

    public FilterRes getFilters() {
        FilterRes filterRes = new FilterRes(this.filters.getFilterKey());
        return filterRes;
    }


    public List<Long> getFiteredTemplestayCategory(TemplestayFilterReqTemp filter) {

        this.filters = new Filters(filter);

        List<Long> contentFilteredId = templestayRepository.findIdsByTempleNameContaining(filter.content());
        List<Category> categoryEntityList = categoryRepository.findAll();
        List<Long> filteredId = filters.getFilteredCategory(categoryEntityList);
        filteredId.retainAll(contentFilteredId);
        return filteredId;
    }


    public FilterCountRes getFilteredTemplestayNum(List<Long> filteredId){

        FilterCountRes filterCountRes = new FilterCountRes(filteredId.size());
        return filterCountRes;
    }


    public PageTemplestayRes getFilteredTemplestay(List<Long> ids, int page, int size, Long userId) {

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

            boolean liked = false;
            if(userId != null){
                liked = wishlistRepository.existsByMemberIdAndTemplestayId(userId, id);
            }

            return new TemplestayRes(id, templeName, organizedName, tag, region, type, imgUrl, liked);

        });

        return new PageTemplestayRes(templestayResListPage.getNumber() + 1, templestayResListPage.getSize(), templestayResListPage.getTotalPages(), templestayResListPage.getContent());
    }


    public ResetFilterRes getFilterReset() {
        ResetFilterRes resetFilterRes = new ResetFilterRes(this.templestayRepository.count(), this.filters.getResetFilter());
        return resetFilterRes;
    }

}