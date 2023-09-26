package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
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

    @Transactional
    public void updateItem(Long itemId, Book param){
        Item findItem = itemRepository.findOne(itemId);

        findItem.setName(param.getName());
        findItem.setPrice(param.getPrice());
        findItem.setStockQuantity(param.getStockQuantity());
    }
    // 영속성 컨텍스트에서 엔티티를 다시 조회한 후에 데이터를 수정하는 방법
    // 트랜잭션 안에서 엔티티 다시 조회, 변경할 값 선택
    // -> 트랜잭션 commit 시점에 JPA 가 flush 를 날리면서 변경된 것들을 다 찾음
    // -> 즉, 변경 감지(Dirty Checking) 동작해서 DB 에 UPDATE SQL 실행
    // -> 따라서 itemRepository.save(findItem) 해줄 필요X

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

// 변경 감지 vs 병합(merge)
// (주의) 변경 감지 기능 사용하면 원하는 속성만 선택해서 변경할 수 있지만
// merge 사용하면 모든 속성이 변경됨, 병합 시 값이 없으면 null 로 업데이트할 위험 존재 (병합은 모든 필드 교체)
// Ex) Item 가격은 무조건 고정이라는 조건이 주어지면, ItemController 의 updateItem 메서드에서 book.setPrice(form.getPrice()) 코드 사용X
//     그런데, 이 상태에서 merge 를 하면 book 객체의 price 필드에 null 값이 들어가게 됨

// -> merge 말고 '변경 감지'를 사용해서 업데이트하려는 필드에만 setXXX 사용하자! (사실 setter 도 거의 사용 말고, 의미 있는 메서드를 직접 만들어 사용하자)