package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // Spring Container 안에서 테스트를 돌리기 위함
@Transactional // rollback 되는 것이 default (rollback 안 되게 하려면 메서드에 @Rollback(false) 설정)
class MemberServiceTest {

    @Autowired MemberService memberService; // test case 에서는 필드 주입 사용 괜찮음
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;
    
    @Test
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("kim");

        // when
        Long saveId = memberService.join(member);

        // then
        assertEquals(member, memberRepository.findOne(saveId));

        // JPA 사용 시 같은 트랜잭션 안에서 PK 값이 똑같으면 같은 영속성 컨텍스트에서 동일한 애가 관리가 됨 (딱 하나로만 관리됨)
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        // when
        memberService.join(member1);
        assertThrows(IllegalStateException.class,
                () -> memberService.join(member2));

        // then
        // Assertions.fail("예외가 발생해야 한다.");
    }

}
// (알게된 점)
// 기본 키 전략이 IDENTITY 면 member 객체의 id 값을 알기 위해서는 INSERT 문이 필요함
// IDENTITY 전략에서는 em.persist 시점에 바로 INSERT SQL 을 실행하게 됨
// -> 그래서 강의와 달리 Rollback(false) 설정 안 해도 INSERT 쿼리문이 로그에 뜨는 것임

// 더 자세한 설명
// JPA 는 영속성 컨텍스트에 엔티티를 등록할 때 항상 PK 값이 있어야 함
// But, 키 생성 전략을 IDENTITY 로 두면 데이터베이스에 INSERT 를 해야 PK 를 구할 수 있음
// (IDENTITY 전략이라는 것이 데이터베이스에 PK 생성을 위임하는 것이기 때문)
// 대표적으로 MySQL 의 auto increment 가 있음
// -> 그래서 IDENTITY 전략은 PK 를 구하기 위해 어쩔 수 없이 DB 에 INSERT 를 강제로 먼저 하게 됨
// 나머지 전략은 DB 에 INSERT 를 강제로 하지 않음