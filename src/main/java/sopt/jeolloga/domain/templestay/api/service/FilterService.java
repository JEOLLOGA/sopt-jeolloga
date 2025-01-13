package sopt.jeolloga.domain.templestay.api.service;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import sopt.jeolloga.common.Filters;
import sopt.jeolloga.domain.templestay.api.dto.FilterRequestDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilterService {

    private final Filters filters;

    public FilterService(Filters filters) {
        this.filters = filters;
    }

    public Map<String, List<String>> getFilters() {
        // Filters 데이터를 Map 형태로 반환

        Map<String, List<String>> filterMap = new HashMap<>();
        filterMap.put("region", filters.getRegion());
        filterMap.put("type", filters.getType());
        filterMap.put("purpose", filters.getPurpose());
        filterMap.put("activity", filters.getActivity());
        filterMap.put("etc", filters.getEtc());
        return filterMap;
    }
}
