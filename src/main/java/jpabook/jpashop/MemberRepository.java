package jpabook.jpashop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(Member member){
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id){
        return em.find(Member.class, id);
    }
}
// CQS 원칙(Command Query Separation) : Command(set) 와 Query(get) 는 분리되어야 한다
// 메소드 호출 시, 내부에서 변경(side effect) 발생하는 메서드인지, 내부에서 변경 전혀 일어나지 않는 메서드인지 명확히 분리
// -> 데이터 변경 관련 이슈 발생했을 때, 변경이 일어나는 메서드만 찾아보면 된다는 이점 존재
// save 는 Command 이므로, return 값 없는게 좋지만, id 만 반환하는 건 괜찮음 (Member 객체를 반환하는 것은 X)