package sopt.jeolloga.domain.templestay.api.dto;

public record TemplestayRankingRes(
        int ranking,
        Long id,
        String templeName,
        String templestayName,
        String tag,
        String region,
        boolean liked,
        String imgUrl
) {
}
