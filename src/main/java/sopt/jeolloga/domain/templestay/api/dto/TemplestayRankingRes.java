package sopt.jeolloga.domain.templestay.api.dto;

public record TemplestayRankingRes(
        int ranking,
        Long templestayId,
        String templeName,
        String tag,
        String region,
        boolean liked,
        String imgUrl
) {
}
