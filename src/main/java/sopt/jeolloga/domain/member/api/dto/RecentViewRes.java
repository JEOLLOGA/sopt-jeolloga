package sopt.jeolloga.domain.member.api.dto;

import sopt.jeolloga.domain.templestay.api.dto.TemplestayRes;

import java.util.List;

public record RecentViewRes(List<TemplestayRes> templestays) {
}
