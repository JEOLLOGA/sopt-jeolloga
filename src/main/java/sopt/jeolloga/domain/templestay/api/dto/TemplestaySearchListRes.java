package sopt.jeolloga.domain.templestay.api.dto;

import java.util.List;

public record TemplestaySearchListRes(
        List<TemplestaySearchRes> templestays
) {
    public static TemplestaySearchListRes of(List<TemplestaySearchRes> templestays) {
        return new TemplestaySearchListRes(templestays);
    }
}
