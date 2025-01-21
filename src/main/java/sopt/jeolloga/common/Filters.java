package sopt.jeolloga.common;

import org.springframework.stereotype.Component;
import sopt.jeolloga.domain.templestay.core.Category;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Filters {

    private static final int DEFAULT_MAX_PRICE = Integer.MAX_VALUE;
    private static final int DEFAULT_MIN_PRICE = 0;

    private final List<String> regionOptions = List.of("강원", "경기", "경남", "경북", "광주", "대구", "대전", "부산", "서울", "인천", "전남", "전북", "제주", "충남", "충북");
    private final List<String> typeOptions = List.of("당일형", "휴식형", "체험형");
    private final List<String> purposeOptions = List.of("힐링", "전통문화 체험", "심신치유", "자기계발", "여행 일정", "사찰순례", "휴식", "호기심");
    private final List<String> activityOptions = List.of("발우공양", "108배", "스님과의 차담", "등산", "새벽 예불", "사찰 탐방", "염주 만들기", "연등 만들기", "다도", "명상", "산책", "요가", "기타");
    private final List<String> etcOptions = List.of("절밥이 맛있는", "TV에 나온", "연예인이 다녀간", "근처 관광지가 많은", "속세와 멀어지고 싶은", "동물 친구들과 함께", "유튜브 운영 중인", "단체 가능");

    private Map<String, Object> regionFilter;
    private Map<String, Object> typeFilter;
    private Map<String, Object> purposeFilter;
    private Map<String, Object> activityFilter;
    private Map<String, Object> priceFilter;
    private Map<String, Object> etcFilter;

    public Filters() {
        this.regionFilter = initializeFilter(this.regionOptions);
        this.typeFilter = initializeFilter(this.typeOptions);
        this.purposeFilter = initializeFilter(this.purposeOptions);
        this.activityFilter = initializeFilter(this.activityOptions);
        this.etcFilter = initializeFilter(this.etcOptions);
        this.priceFilter = initializePriceFilter(DEFAULT_MIN_PRICE, DEFAULT_MAX_PRICE);
    }

    public Filters(Map<String, Object> filter) {

        this.regionFilter = (Map<String, Object>) filter.get("region");
        this.typeFilter = (Map<String, Object>) filter.get("type");
        this.purposeFilter = (Map<String, Object>) filter.get("purpose");
        this.activityFilter = (Map<String, Object>) filter.get("activity");
        this.priceFilter = (Map<String, Object>) filter.get("price");
        priceFilter.put("maxPrice", priceFilter.get("maxPrice").equals(300000) ? Integer.MAX_VALUE : priceFilter.get("maxPrice"));
        this.etcFilter = (Map<String, Object>) filter.get("etc");
    }

    private Map<String, Object> initializeFilter(List<String> options) {
        return options.stream()
                .collect(Collectors.toMap(
                        option -> option,
                        option -> 1,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    private Map<String, Object> initializePriceFilter(int minPrice, int maxPrice) {
        Map<String, Object> priceFilter = new LinkedHashMap<>();
        priceFilter.put("minPrice", minPrice);
        priceFilter.put("maxPrice", maxPrice);
        return priceFilter;
    }

    public void setFilterValue(Map<String, Object> filter, int value) {
        filter.replaceAll((key, oldValue) -> value);
    }

    public Map<String, Object> getResetFilter() {

        setFilterValue(regionFilter, 0);
        setFilterValue(typeFilter, 0);
        setFilterValue(purposeFilter, 0);
        setFilterValue(activityFilter, 0);
        setFilterValue(etcFilter, 0);

        Map<String, Object> resetPriceFilter = initializePriceFilter(DEFAULT_MIN_PRICE, 300000);

        LinkedHashMap<String, Object> resetFilter = new LinkedHashMap<>();
        resetFilter.put("region", regionFilter);
        resetFilter.put("type", typeFilter);
        resetFilter.put("purpose", purposeFilter);
        resetFilter.put("activity", activityFilter);
        resetFilter.put("etc", etcFilter);
        resetFilter.put("price", resetPriceFilter);

        return resetFilter;
    }


    public List<Long> getFilteredCategory(List<Category> categoryEntities) {
        Integer binaryRegionFilter = convertToBinaryFilter(regionFilter, regionOptions);
        Integer binaryTypeFilter = convertToBinaryFilter(typeFilter, typeOptions);
        Integer binaryPurposeFilter = convertToBinaryFilter(purposeFilter, purposeOptions);
        Integer binaryActivityFilter = convertToBinaryFilter(activityFilter, activityOptions);
        Integer binaryEtcFilter = convertToBinaryFilter(etcFilter, etcOptions);


        Integer minPrice = (Integer) priceFilter.getOrDefault("minPrice", DEFAULT_MIN_PRICE);
        Integer maxPrice = (Integer) priceFilter.getOrDefault("maxPrice", DEFAULT_MAX_PRICE);

        return categoryEntities.stream()
                .filter(category -> matchesFilter(category, binaryRegionFilter, binaryTypeFilter, binaryPurposeFilter, binaryActivityFilter, binaryEtcFilter, minPrice, maxPrice))
                .map(Category::getId)
                .collect(Collectors.toList());
    }

    private boolean matchesFilter(Category category, int binaryRegionFilter, int binaryTypeFilter, int binaryPurposeFilter, int binaryActivityFilter, int binaryEtcFilter, int minPrice, int maxPrice) {
        return (category.getRegion() & binaryRegionFilter) != 0 &&
                (category.getType() & binaryTypeFilter) != 0 &&
                (category.getPurpose() & binaryPurposeFilter) != 0 &&
                (category.getActivity() & binaryActivityFilter) != 0 &&
                (category.getEtc() & binaryEtcFilter) != 0 &&
                category.getPrice() >= minPrice &&
                category.getPrice() <= maxPrice;
    }

    public Integer convertToBinaryFilter(Map<String, Object> filterMap, List<String> options) {

        Integer binaryValue = 0;
        int position = 0;

        for (String option : options) {

            if((Integer) filterMap.get(option) == 1) {
                binaryValue |= (1 << position);
            }
            position ++;
        }

        if(binaryValue == 0){
            return Integer.MAX_VALUE;
        }
        return binaryValue;
    }

    public String getFilterKey(int binaryFilter, Map<String, Object> filterMap) {

        String filterKey = "";
        int position = 0;

        for (Map.Entry<String, Object> entry : filterMap.entrySet()) {
            if ((binaryFilter & (1 << position)) != 0) {
                filterKey = entry.getKey();
                break;
            }
            position++;
        }
        return filterKey;
    }

    private Map<String, Object> convertToResponse(Category category) {

        Map<String, Object> response = new HashMap<>();
        response.put("id", category.getId());
        response.put("type", category.getType());
        response.put("region", category.getRegion());
        return response;
    }

    public Map<String, List<String>> getFilterKey() {

        Map<String, Map<String, Object>> filters = new LinkedHashMap<>();
        filters.put("region", this.regionFilter);
        filters.put("type", this.typeFilter);
        filters.put("purpose", this.purposeFilter);
        filters.put("activity", this.activityFilter);
        filters.put("etc", this.etcFilter);

        Map<String, List<String>> filterKeys = filters.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new ArrayList<>(entry.getValue().keySet())
                ));

        return filterKeys;
    }

    public Map<String, Object> getTypeFilter() {
        return typeFilter;
    }

    public Map<String, Object> getRegionFilter() {
        return regionFilter;
    }

}


