package sopt.jeolloga.domain.member.api.service;

import org.springframework.stereotype.Service;
import sopt.jeolloga.domain.member.api.dto.MemberDetailRes;
import sopt.jeolloga.domain.member.api.dto.MemberNameRes;
import sopt.jeolloga.domain.member.api.dto.MemberReq;
import sopt.jeolloga.domain.member.api.dto.MemberRes;
import sopt.jeolloga.domain.member.core.Member;
import sopt.jeolloga.domain.member.core.MemberRepository;
import sopt.jeolloga.domain.member.core.exception.MemberCoreException;
import sopt.jeolloga.exception.ErrorCode;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    public Long findOrCreateUser(MemberRes memberInfo) {

        System.out.println(memberInfo);

        return memberRepository.findByKakaoUserId(memberInfo.userId())
                .map(Member::getId)
                .orElseGet(() -> {
                    Member newMember = new Member(memberInfo.userId(), memberInfo.nickname(), memberInfo.email(), null, null, null, null);
                    memberRepository.save(newMember);
                    System.out.println("New User Created");
                    return newMember.getId();
                });
    }

    public void saveInfo(String accessToken, MemberReq memberReq){

        Member member = memberRepository.findById(memberReq.userId())
                .orElseThrow(() -> new MemberCoreException(ErrorCode.NOT_FOUND_USER));

        member.setNickname(member.getNickname());
        member.setEmail(member.getEmail());
        member.setAgeRange(memberReq.ageRange());
        member.setGender(memberReq.gender());
        member.setReligion(memberReq.religion());

        if("있음".equals(memberReq.hasExperience())){
            member.setHasExperience(true);
        } else if("없음".equals(memberReq.hasExperience())) {
            member.setHasExperience(false);
        } else {
            member.setHasExperience(false);
        }

        memberRepository.save(member);
    }

    public MemberDetailRes getMember(String accessToken, Long userId) {

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberCoreException(ErrorCode.NOT_FOUND_USER));

        MemberDetailRes memberDetailRes = new MemberDetailRes(member.getId(), member.getNickname(), member.getEmail(), member.getAgeRange(), member.getGender() , member.getReligion(), member.getHasExperience());
        return memberDetailRes;
    }

    public MemberNameRes getMemberName(String accessToken, Long userId) {

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberCoreException(ErrorCode.NOT_FOUND_USER));

        MemberNameRes memberNameRes = new MemberNameRes(member.getNickname());
        return memberNameRes;
    }
}
