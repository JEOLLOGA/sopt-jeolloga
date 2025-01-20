package sopt.jeolloga.domain.member.api.service;

import org.springframework.stereotype.Service;
import sopt.jeolloga.domain.member.api.dto.MemberDetailRes;
import sopt.jeolloga.domain.member.api.dto.MemberNameRes;
import sopt.jeolloga.domain.member.api.dto.MemberReq;
import sopt.jeolloga.domain.member.api.dto.MemberRes;
import sopt.jeolloga.domain.member.api.utils.JwtTokenProvider;
import sopt.jeolloga.domain.member.core.Member;
import sopt.jeolloga.domain.member.core.MemberRepository;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider){
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void saveInfo(String accessToken, MemberReq memberReq){

        Member member = memberRepository.findById(memberReq.userId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found for id: " + memberReq.userId()));

        member.setNickname(member.getNickname());
        member.setEmail(member.getEmail());
        member.setAgeRange(memberReq.ageRange());
        member.setGender(memberReq.gender());
        member.setReligion(memberReq.religion());
        member.setHasExperience(memberReq.hasExperience());

        memberRepository.save(member);
    }

    public MemberDetailRes getMember(String accessToken, Long userId) {

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found for id: " + userId));

        MemberDetailRes memberDetailRes = new MemberDetailRes(member.getId(), member.getNickname(), member.getEmail(), member.getAgeRange(), member.getGender() , member.getReligion(), member.getHasExperience());
        return memberDetailRes;
    }

    // 특정 유저 조회
    public MemberNameRes getMemberName(String accessToken, Long userId) {

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nickname not found for id: " + userId));

        MemberNameRes memberNameRes = new MemberNameRes(member.getNickname());
        return memberNameRes;
    }

    // Access Token에서 추출한 ID와 요청 ID가 동일한지 검증 -> 재논의 필요
    private boolean isEqualId(String accessToken, Long id){
        String tokenId = jwtTokenProvider.getMemberIdFromToken(accessToken);
        return String.valueOf(id).equals(tokenId);
    }
}
