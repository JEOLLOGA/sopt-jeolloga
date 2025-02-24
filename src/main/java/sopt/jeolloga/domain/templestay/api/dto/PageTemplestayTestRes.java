package sopt.jeolloga.domain.templestay.api.dto;

import java.util.List;

public record PageTemplestayTestRes(int Page, int pageSize, int totalPages, List<TemplestayTestRes> templestays) {
}
