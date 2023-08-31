package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    // 회원 등록
    public void save(Member member){
        em.persist(member); // persist 되면 영속성 컨텍스트에서 객체는 PK 값을 가짐 (DB 에 들어간 시점이 아니어도!)
    }

    // 회원 조회
    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    // 회원 리스트 조회
    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    // 회원 조회 (이름 기준)
    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    } // 단일 결과를 받는 getSingleResult() 는 일치하는 것이 하나도 없거나, 2개 이상이면 Exception 을 던짐
    // service 에서 중복되는 name 이 없도록 구현한다면 getResultList() 를 사용해도 좋음
}
// Spring Data JPA 가 @PersistenceContext 대신에 @Autowired 를 사용할 수 있게 지원해줌
// -> 그러면, @RequiredArgsConstructor 를 사용해서 EntityManager 를 생성자 주입으로 사용 가능