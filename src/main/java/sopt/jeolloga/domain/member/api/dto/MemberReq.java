package sopt.jeolloga.domain.member.api.dto;


public record MemberReq(Long id, String ageRange, String gender, String religion, boolean hasExperience) {
}
