package sopt.jeolloga.domain.member.api.service;

import org.springframework.stereotype.Service;
import sopt.jeolloga.domain.member.api.dto.MemberNameRes;
import sopt.jeolloga.domain.member.api.dto.MemberReq;
import sopt.jeolloga.domain.member.api.dto.MemberRes;
import sopt.jeolloga.domain.member.core.Member;
import sopt.jeolloga.domain.member.core.MemberRepository;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    public void saveInfo(MemberReq memberReq){

        Member member = memberRepository.findById(memberReq.id())
                .orElseThrow(() -> new IllegalArgumentException("Member not found for id: " + memberReq.id()));

        // 요청 데이터를 사용하여 엔티티의 값 업데이트
        member.setNickname(member.getNickname());
        member.setEmail(member.getEmail());
        member.setAgeRange(memberReq.ageRange());
        member.setGender(memberReq.gender());
        member.setReligion(memberReq.religion());
        member.setHasExperience(memberReq.hasExperience());

        // 엔티티 저장
        memberRepository.save(member);
    }

    public MemberRes getMember(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found for id: " + id));
        MemberRes memberRes = new MemberRes(member.getId(), member.getNickname(), member.getEmail(), member.getAgeRange(), member.getGender() , member.getReligion(), member.getHasExperience());
        return memberRes;
    }

    // 특정 유저 조회
    public MemberNameRes getMemberName(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nickname not found for id: " + id));

        MemberNameRes memberNameRes = new MemberNameRes(member.getNickname());
        return memberNameRes;
    }

}
