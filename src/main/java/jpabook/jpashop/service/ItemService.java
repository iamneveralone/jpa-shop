package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional // 이 메서드는 '데이터 변경'하므로 readOnly = false
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }
}
// 이 경우에, ItemService 는 단순히 ItemRepository 에 위임만 하는 역할
// -> 경우에 따라서, Controller 에서 바로 ItemRepository 접근하도록 설계할 수도 있음

// 클래스, 메서드 모두에 @Transactional 붙이면 메서드 레벨의 @Transactional 선언이 우선 적용됨
// @Transactional 이 붙은 메서드는 메서드가 포함하고 있는 작업 중에 하나라도 실패할 경우 전체 작업 취소

// @Transactional 은 메서드에 대해서, Spring 은 해당 메서드에 대한 프록시를 만듦
// (프록시 패턴 : 어떤 코드를 감싸면서 추가적인 연산을 수행하도록 강제하는 방법)
// (-> 프록시 객체 : 원래 객체를 감싸고 있는 같은 타입의 객체, 접근을 제어하고 싶거나 부가 기능 추가하고 싶을 때 주로 사용)
// 트랜잭션의 경우, 트랜잭션의 시작과 연산 종료 시의 커밋 과정이 필요하므로, 프록시를 생성해 해당 메서드의 앞뒤에 트랜잭션의 시작과 끝을 추가하는 것!

// 스프링 컨테이너는 '트랜잭션 범위의 영속성 컨텍스트 전략'을 기본으로 사용
// 서비스 클래스에서 @Transactional 사용할 경우, 해당 코드 내의 메서드를 호출할 때 영속성 컨텍스트가 생긴다는 뜻
// 영속성 컨텍스트는 프록시(트랜잭션 AOP)가 트랜잭션을 시작할 때 생겨나고, 메서드가 종료되어 프록시가 트랜잭션을 커밋할 경우 영속성 컨텍스트가 flush 되면서 해당 내용 반영
// 이후, 영속성 컨텍스트 역시 종료되는 것

// "프록시"
// 1. 클라이언트로부터 타겟을 대신해서 요청을 받는 대리인
// 2. 실제 오브젝트인 타겟은 프록시를 통해 최종적으로 요청받아 처리함
// 3. 따라서 타겟은 자신의 기능에만 집중하고 부가기능은 프록시에게 위임함