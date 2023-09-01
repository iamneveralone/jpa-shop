package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){
        if (item.getId() == null){ // item 은 JPA 에 저장하기 전까지 Id 값이 없음 (= 새로 생성한 객체)
            em.persist(item);
        }
        else{ // 기존에 DB 에 해당 item 이 존재하는 경우
            em.merge(item); // update 와 비슷한 느낌이라고 생각하면 됨
        }
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
