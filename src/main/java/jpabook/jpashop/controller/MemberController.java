package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model){
        model.addAttribute("memberForm", new MemberForm()); // controller 에서 view 로 넘어갈 때 데이터를 model 에 담아 전달
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm memberForm, BindingResult result) {

        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());

        Member member = new Member();
        member.setName(memberForm.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/"; // 첫번째 페이지로 다시 넘어감
    }
    // 만약 MemberForm 쪽에서 오류가 발생하면 원래는 튕겨버리는데,
    // Validate 한 다음에 BindingResult 가 있으면 오류가 BindingResult 에 담겨서 코드가 실행됨

    @GetMapping("/members")
    public String list(Model model){
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
// API 를 만들 땐 절대 Entity 를 외부에 반환하면 안 됨 -> 꼭 DTO 사용하자!!
// API 는 스펙이다. 만약에 Entity 에 password 필드를 추가
// -> 1. password 가 그대로 노출되는 문제 발생
// -> 2. API 스펙이 변함 (불완전 API 스펙이 됨)
