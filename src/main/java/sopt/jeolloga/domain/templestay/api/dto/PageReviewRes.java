package sopt.jeolloga.domain.templestay.api.dto;

import java.util.List;

public record PageReviewRes<T>(
        Long templestayId,
        int page,
        int pageSize,
        int totalPages,
        List<T> reviews
) {}
