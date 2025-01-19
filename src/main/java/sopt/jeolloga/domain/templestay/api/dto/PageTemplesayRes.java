package sopt.jeolloga.domain.templestay.api.dto;

import java.util.List;

public record PageTemplesayRes(int Page, int pageSize, int totalPages, List<TemplestayRes> templestays) {
}
