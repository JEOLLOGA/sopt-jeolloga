package sopt.jeolloga.domain.templestay.api.dto;

import java.util.List;

public record TemplestayRankingListRes(
        List<TemplestayRankingRes> rankings
) {
    public static TemplestayRankingListRes of(List<TemplestayRankingRes> rankings) {
        return new TemplestayRankingListRes(rankings);
    }
}