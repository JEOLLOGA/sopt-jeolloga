package sopt.jeolloga.domain.member.api.dto;

public record MemberRes(Long id, String nickname, String email, String ageRange, String gender, String religion, boolean hasExperience) {

}
