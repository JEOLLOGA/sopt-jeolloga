package sopt.jeolloga.domain.member.api.service;

import org.springframework.stereotype.Service;
import sopt.jeolloga.domain.member.api.dto.*;
import sopt.jeolloga.domain.member.core.Member;
import sopt.jeolloga.domain.member.core.MemberRepository;
import sopt.jeolloga.domain.member.core.exception.MemberCoreException;
import sopt.jeolloga.exception.ErrorCode;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final WebhookService webhookService;

    public MemberService(MemberRepository memberRepository, WebhookService webhookService) {
        this.webhookService = webhookService;
        this.memberRepository = memberRepository;
    }

    public LoginRes findOrCreateUser(MemberRes memberInfo) {
        // kakao user id 기반으로 서비스 가입 여부 판단 후 유저 조회 or 생성
        return memberRepository.findByKakaoUserId(memberInfo.userId())
                .map(existingMember -> new LoginRes(existingMember.getId(), existingMember.getNickname()))
                .orElseGet(() -> {
                    Member newMember = new Member(
                            memberInfo.userId(),
                            memberInfo.nickname(),
                            memberInfo.email(),
                            null, null, null, null
                    );
                    memberRepository.save(newMember);
                    webhookService.sendDiscordNotification();
                    return new LoginRes(newMember.getId(), null);
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

        Boolean hasExperience = member.getHasExperience();
        if (hasExperience == null) {
            hasExperience = false;
        }

        MemberDetailRes memberDetailRes = new MemberDetailRes(
                member.getId(),
                member.getNickname(),
                member.getEmail(),
                member.getAgeRange(),
                member.getGender(),
                member.getReligion(),
                hasExperience 
        );

        return memberDetailRes;
    }

    public MemberNameRes getMemberName(String accessToken, Long userId) {

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberCoreException(ErrorCode.NOT_FOUND_USER));

        MemberNameRes memberNameRes = new MemberNameRes(member.getNickname());
        return memberNameRes;
    }

    public Long getUserKakaoId(Long userId){
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberCoreException(ErrorCode.NOT_FOUND_USER));
        return member.getKakaoUserId();
    }

    public void deleteMemberById(Long userId){
        if (!memberRepository.existsById(userId)) {
            throw new MemberCoreException(ErrorCode.NOT_FOUND_USER);
        }
        memberRepository.deleteById(userId);
    }
}
