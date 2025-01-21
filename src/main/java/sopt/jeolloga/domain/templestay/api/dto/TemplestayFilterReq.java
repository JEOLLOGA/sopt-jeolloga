package sopt.jeolloga.domain.templestay.api.dto;

import java.util.Map;

public record TemplestayFilterReq(
        Long userId,
        String content,
        Map<String, Integer> region,
        Map<String, Integer> type,
        Map<String, Integer> purpose,
        Map<String, Integer> activity,
        PriceFilter price,
        Map<String, Integer> etc
) {
    public record PriceFilter(
            Integer minPrice,
            Integer maxPrice
    ) {}
}

