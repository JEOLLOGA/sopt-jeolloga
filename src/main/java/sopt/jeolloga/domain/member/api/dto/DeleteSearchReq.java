package sopt.jeolloga.domain.member.api.dto;

public record DeleteSearchReq(
        Long userId,
        Long searchId
) {
}
