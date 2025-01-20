package sopt.jeolloga.domain.member.api.dto;


public record MemberReq(Long userId, String ageRange, String gender, String religion, boolean hasExperience) {
}
