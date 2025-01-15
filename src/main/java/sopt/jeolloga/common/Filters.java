package sopt.jeolloga.common;

import org.springframework.stereotype.Component;
import sopt.jeolloga.domain.templestay.core.CategoryEntity;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Filters {

    // naming convention
    // filter : json type individual category filter
    // unified filter : json type unified category filter
    // binary filter : binary type individual category filter

    private Map<String, Object> regionFilter;
    private Map<String, Object> typeFilter;
    private Map<String, Object> purposeFilter;
    private Map<String, Object> activityFilter;
    private Map<String, Object> priceFilter;
    private Map<String, Object> etcFilter;
//    private Map<String, Object> resetFilter;

    public Filters() {

        this.regionFilter = Arrays.asList("강원", "경기", "경남", "경북", "광주", "대구", "대전", "부산", "서울", "인천", "전남", "전북", "제주", "충남", "충북").stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> 1
                ));

        this.typeFilter = Arrays.asList("당일형", "휴식형", "체험형").stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> 1
                ));

        this.purposeFilter = Arrays.asList("힐링", "전통문화 체험", "심신치유", "자기계발", "여행 일정", "사찰순례", "휴식", "호기심", "기타").stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> 1
                ));

        this.activityFilter = Arrays.asList("발우공양", "108배", "스님과의 차담", "등산", "새벽 예불", "사찰 탐방", "염주 만들기", "연등 만들기", "다도", "명상", "산책", "요가", "기타").stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> 1
                ));

        this.priceFilter = new HashMap<String, Object>();
        this.priceFilter.put("minPrice", 0);
        this.priceFilter.put("maxPrice", Integer.MAX_VALUE);

        this.etcFilter = Arrays.asList("절밥이 맛있는", "TV에 나온", "연예인이 다녀간", "근처 관광지가 많은", "속세와 멀어지고 싶은", "동물 친구들과 함께", "유튜브 운영 중인", "단체 가능").stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> 1
                ));

        // resetFilter setting
//        this.resetFilter = new HashMap<>();
//        resetFilter.put("region", this.regionFilter);
//        resetFilter.put("type", typeFilter);
//        resetFilter.put("purpose", purposeFilter);
//        resetFilter.put("activity", activityFilter);

//        Map<String, Object> resetPriceFilter = new HashMap<>();
//        resetPriceFilter.put("minPrice",0);
//        resetPriceFilter.put("maxPrice", 300000);

//        resetFilter.put("price", resetPriceFilter);
//        resetFilter.put("etc", etcFilter);
    }

    // Separate client's unified filter request into individual filters
    public Filters(Map<String, Object> unifiedFilter) {

        this.regionFilter = (Map<String, Object>) unifiedFilter.get("region");
        this.typeFilter = (Map<String, Object>) unifiedFilter.get("type");
        this.purposeFilter = (Map<String, Object>) unifiedFilter.get("purpose");
        this.activityFilter = (Map<String, Object>) unifiedFilter.get("activity");

        this.priceFilter = (Map<String, Object>) unifiedFilter.get("price");
        if(priceFilter.get("maxPrice").equals(300000)){
            priceFilter.put("maxPrice", Integer.MAX_VALUE);
        }

        this.etcFilter = (Map<String, Object>) unifiedFilter.get("etc");
    }

    public void setFilter(Integer value){

        this.regionFilter = Arrays.asList("강원", "경기", "경남", "경북", "광주", "대구", "대전", "부산", "서울", "인천", "전남", "전북", "제주", "충남", "충북").stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> value
                ));

        this.typeFilter = Arrays.asList("당일형", "휴식형", "체험형").stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> value
                ));

        this.purposeFilter = Arrays.asList("힐링", "전통문화 체험", "심신치유", "자기계발", "여행 일정", "사찰순례", "휴식", "호기심", "기타").stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> value
                ));

        this.activityFilter = Arrays.asList("발우공양", "108배", "스님과의 차담", "등산", "새벽 예불", "사찰 탐방", "염주 만들기", "연등 만들기", "다도", "명상", "산책", "요가", "기타").stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> value
                ));

        this.priceFilter = new HashMap<String, Object>();
        this.priceFilter.put("minPrice", 0);
        this.priceFilter.put("maxPrice", Integer.MAX_VALUE);

        this.etcFilter = Arrays.asList("절밥이 맛있는", "TV에 나온", "연예인이 다녀간", "근처 관광지가 많은", "속세와 멀어지고 싶은", "동물 친구들과 함께", "유튜브 운영 중인", "단체 가능").stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> value
                ));
    }

    public Map<String, List<String>> getFilterKey() {

        // 필터들을 담은 맵
        Map<String, Map<String, Object>> filters = new HashMap<>();
        filters.put("region", this.regionFilter);
        filters.put("type", this.typeFilter);
        filters.put("purpose", this.purposeFilter);
        filters.put("activity", this.activityFilter);
        filters.put("etc", this.etcFilter);

        // 키 추출 및 변환
        Map<String, List<String>> filterKeys = filters.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, // 필터의 이름 (region, type 등)
                        entry -> new ArrayList<>(entry.getValue().keySet()) // 해당 필터의 key 리스트
                ));

        return filterKeys;
    }

    // convert filter into binary filter
    public Integer convertToBinaryFilter(Map<String, Object> filterMap) {

        Integer binaryValue = 0;
        int position = 0;

        // 맵의 key를 순회하며 값을 2진수로 변환
        for(Map.Entry<String, Object> entry : filterMap.entrySet()) {
            if((Integer) entry.getValue() == 1) {
                binaryValue |= (1 << position); // set 1
            }
            position ++;
        }
        return binaryValue;
    }

    // convert region binary filter into region filter key
    public String getRegionFilterKey(int binaryFilter) {

        String regionName = "";
        int position = 0;

        // key-value enumerate
        for (Map.Entry<String, Object> entry : this.regionFilter.entrySet()) {
            if ((binaryFilter & (1 << position)) != 0) {
                regionName = entry.getKey();
                break;
            }
            position++;
        }
        return regionName;
    }

    // convert type binary filter into type filter key
    public String getTypeFilterKey(int binaryFilter) {

        String typeName = "";
        int position = 0;

        // key-value enumerate
        for (Map.Entry<String, Object> entry : this.typeFilter.entrySet()) {
            if ((binaryFilter & (1 << position)) != 0) {
                typeName = entry.getKey();
                break;
            }
            position++;
        }
        return typeName;
    }

    public Map<String, Object> getResetFilter() {

        setFilter(0);

        Map<String, Object> resetFilter = new HashMap<>();
        resetFilter.put("region", this.regionFilter);
        resetFilter.put("type", typeFilter);
        resetFilter.put("purpose", purposeFilter);
        resetFilter.put("activity", activityFilter);
        resetFilter.put("etc", etcFilter);

        Map<String, Object> resetPriceFilter = new HashMap<>();
        resetPriceFilter.put("minPrice",0);
        resetPriceFilter.put("maxPrice", 300000);

        resetFilter.put("price", resetPriceFilter);

        return resetFilter;
    }

    public Object getMinPrice() {
        return this.priceFilter.get("minPrice");
    }

    public Object getMaxPrice() {
        return this.priceFilter.get("maxPrice");
    }

    public List<Long> getFilteredCategory(List<CategoryEntity> categoryEntityList) {

        // client's binary filter
        Integer requestBinaryRegionFilter = convertToBinaryFilter(this.regionFilter);
        Integer requestBinaryTypeFilter = convertToBinaryFilter(this.typeFilter);
        Integer requestBinaryPurposeFilter = convertToBinaryFilter(this.purposeFilter);
        Integer requestBinaryActivityFilter = convertToBinaryFilter(this.activityFilter);
        Integer requestMinPrice = Optional.ofNullable((Integer) this.priceFilter.get("minPrice")).orElse(0);
        Integer requestMaxPrice = Optional.ofNullable((Integer) this.priceFilter.get("maxPrice")).orElse(0);
        Integer requestBinaryEtcFilter = convertToBinaryFilter(this.etcFilter);

        // 필터링 로직
        return categoryEntityList.stream()
                .filter(category -> {

                    // database binary filter
                    Integer binaryRegionFilter = Optional.ofNullable(category.getRegion()).orElse(0);
                    Integer binaryTypeFilter = Optional.ofNullable(category.getType()).orElse(0);
                    Integer binaryPurposeFilter = Optional.ofNullable(category.getPurpose()).orElse(0);
                    Integer binaryActivityFilter = Optional.ofNullable(category.getActivity()).orElse(0);
                    Integer binaryEtcFilter = Optional.ofNullable(category.getEtc()).orElse(0);
                    Integer price = Optional.ofNullable(category.getPrice()).orElse(0);

                    // 필터 조건 확인
                    return (binaryRegionFilter & requestBinaryRegionFilter) != 0 &&
                            (binaryTypeFilter & requestBinaryTypeFilter) != 0 &&
                            (binaryPurposeFilter & requestBinaryPurposeFilter) != 0 &&
                            (binaryActivityFilter & requestBinaryActivityFilter) != 0 &&
                            (binaryEtcFilter & requestBinaryEtcFilter) != 0 &&
                            price >= requestMinPrice &&
                            price <= requestMaxPrice;
                })
                .map(CategoryEntity::getId)
                .collect(Collectors.toList());
    }
}
