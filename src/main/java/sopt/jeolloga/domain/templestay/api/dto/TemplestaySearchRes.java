package sopt.jeolloga.domain.templestay.api.dto;

public record TemplestaySearchRes(
        Long templestayId,
        String templeName,
        String templestayName,
        String tag,
        String region,
        String type,
        String imgUrl,
        boolean liked
) {
}

