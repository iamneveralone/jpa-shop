package jpabook.jpashop;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

// @ExtendWith(SpringExtension.class) // -> @SpringBootTest 내부에 포함되어 있음
@SpringBootTest
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Test
    @Transactional // No EntityManager with actual transaction available for current thread 오류 해결
    @Rollback(false) // @Transactional 이 test case 에 있으면 test 가 끝난 후에 바로 DB 를 rollback 해버림 -> false 지정하면 rollback X
    public void testMember() throws Exception {
        // given
        Member member = new Member();
        member.setUsername("memberA");

        // when
        Long saveId = memberRepository.save(member);
        Member findMember = memberRepository.find(saveId);

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId()); // 앞 : actual, 뒤 : expected
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
        System.out.println("findMember = " + findMember);
        System.out.println("member = " + member);
        // 출력 결과
        // findMember = jpabook.jpashop.Member@3b11deb6
        // member = jpabook.jpashop.Member@3b11deb6
        // 같은 영속성 컨텍스트 안에서는 id(식별자) 값이 같으면 같은 entity 로 식별함
        // "어? 영속성 컨텍스트에 있네?"하고 1차 캐시에서 꺼내옴
    }

}