package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    // 회원 등록
    public void save(Member member){
        em.persist(member);
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
