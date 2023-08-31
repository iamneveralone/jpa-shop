package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 가입
    @Transactional
    public Long join(Member member){
        validateDuplicateMember(member); // 중복 회원 검증 (문제 있으면 예외 발생)
        memberRepository.save(member); // (문제 없으면 회원 정상적으로 save)
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}
// JPA 의 모든 데이터 변경 또는 로직은 트랜잭션 안에서 실행되어야 함 -> 클래스 위에 @Transactional 붙이자
// 이 예제에서는 회원 가입을 제외한 중복 회원 검증, 회원 전체 조회, 회원 한 명 조회 로직은 데이터 변경 없이 조회만 함!
// 이런 경우에는 해당 메서드 위에 @Transactional(readOnly = True) 를 붙여주면 성능 조금 더 최적화!
// readOnly = True 를 사용하게 될 메서드가 더 많은 상황이므로, MemberService 클래스 위에 @Transactional(readOnly = True)를 붙여주고
// 데이터 변경이 필요한 '회원 가입' 로직에 따로 @Transactional 붙여주면 됨
// -> 따로 @Transactional 붙여준 메서드는 @Transactional(readOnly = True)보다 @Transactional 이 우선권을 가짐
// ( @Transactional 는 readOnly = false 가 default)

// 만약의 경우에 memberA 가 validateDuplicateMember 로직을 통과해서 둘 다 save 될 수 있음
// 그렇게 되면, DB 에 memberA 가 중복 등록될 수 있음
// -> 실무에서는 DB 에서 name 에 unique 제약 조건을 설정해줌으로써 최후의 방어를 사용하기도 함
// (우리는 지금 같은 이름의 회원 등록할 수 없다는 전제를 깔아두고 있음)