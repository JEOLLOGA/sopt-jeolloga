package sopt.jeolloga.domain.member.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.jeolloga.domain.member.api.dto.MemberNameRes;
import sopt.jeolloga.domain.member.api.dto.MemberReq;
import sopt.jeolloga.domain.member.api.dto.MemberRes;
import sopt.jeolloga.domain.member.api.repository.Member;
import sopt.jeolloga.domain.member.api.service.MemberService;

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }

    @PostMapping("/user/register")
    public ResponseEntity<String> saveInfo(@RequestHeader Long id, @RequestBody MemberReq memberReq) {

        MemberReq memberNameReq = new MemberReq(id, memberReq.ageRange(), memberReq.gender(), memberReq.religion(), memberReq.hasExperience());
        memberService.saveInfo(memberNameReq);

        return ResponseEntity.ok("Member information update sucess");
    }

    @GetMapping("/user/register/success")
    public ResponseEntity<MemberNameRes> getUserName(@RequestHeader Long id) {

        MemberNameRes memberNameRes = memberService.getMemberName(id);
        return ResponseEntity.ok(memberNameRes);
    }

    @GetMapping("/user/mypage")
    public ResponseEntity<MemberRes> getMemeberById(@RequestHeader Long id) {
        MemberRes memberRes = memberService.getMember(id);
        return ResponseEntity.ok(memberRes);
    }

}
