package sopt.jeolloga.login.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.jeolloga.login.repository.Member;
import sopt.jeolloga.login.service.MemberService;
import java.util.List;


@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<List<Member>> getAllmembers() {
        List<Member> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemeberById(@PathVariable Long id) {
        Member member = memberService.getMember(id);
        return ResponseEntity.ok(member);
    }

}
