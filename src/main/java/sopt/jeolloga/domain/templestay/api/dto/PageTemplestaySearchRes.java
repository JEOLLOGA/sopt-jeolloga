package sopt.jeolloga.domain.templestay.api.dto;

import java.util.List;

public record PageTemplestaySearchRes<T>(
        int page,
        int pageSize,
        int totalPages,
        String content,
        List<T> templestays
) {
}
