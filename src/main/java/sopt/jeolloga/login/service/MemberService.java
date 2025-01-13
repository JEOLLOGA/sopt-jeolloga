package sopt.jeolloga.login.service;

import org.springframework.stereotype.Service;
import sopt.jeolloga.login.repository.Member;
import sopt.jeolloga.login.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    // 모든 유저 조회
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    // 특정 유저 조회
    public Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with ID" + id + " not found"));
    }

}
