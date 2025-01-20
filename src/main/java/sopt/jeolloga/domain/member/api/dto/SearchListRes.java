package sopt.jeolloga.domain.member.api.dto;

import java.util.List;

public record SearchListRes(List<SearchRes> searchHistory) {

    public static SearchListRes from(List<Object[]> searchRecords) {
        List<SearchRes> searchDTOs = searchRecords.stream()
                .map(record -> new SearchRes(
                        ((Number) record[0]).longValue(),
                        (String) record[1]
                ))
                .toList();
        return new SearchListRes(searchDTOs);
    }
}
