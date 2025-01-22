package sopt.jeolloga.domain.templestay.api.dto;

import java.util.Map;

public record TemplestayFilterReqTemp(
        String content,
        Map<String, Object> region,
        Map<String, Object> type,
        Map<String, Object> purpose,
        Map<String, Object> activity,
        PriceFilter price,
        Map<String, Object> etc
) {
    public record PriceFilter(
            Integer minPrice,
            Integer maxPrice
    ) {}
}