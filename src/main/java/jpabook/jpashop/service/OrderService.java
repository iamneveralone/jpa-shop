package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /*주문*/
    @Transactional
    public Long order(Long memberId, Long itemId, int count){

        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);

        return order.getId();
    }
    // 원래였으면, delivery 는 DeliveryRepository 를 통해 JPA 에 값을 넣어주고,
    // orderItem 또한 OrderItemRepository 를 사용하는 것이 가장 기본적인 방법일 것이다.
    // But, 우리는 Order 엔티티의 orderItems, delivery 필드에 CASCADE 옵션 적용한 상태
    // -> order 를 persist 할 때, orderItems 및 delivery 도 자동으로 persist 됨!!

    /*주문 취소*/
    @Transactional
    public void cancelOrder(Long orderId){
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);

        // 주문 취소
        order.cancel();
    }
    // order.cancel() 하면 orderItem 의 status 도 변경, Item 의 stockQuantity 값도 변경
    // -> 원래는 이렇게 데이터가 변경되면 추가적으로 직접 Item 의 재고를 증가시키는 SQL 문을 작성해서 DB 에 날려야 했음
    // -> JPA 활용하면 데이터 변경만 해도, JPA 가 변경 내역 감지(Dirty checking)를 통해 알아서 UPDATE 문을 DB에 날려줌

    /*검색*/
    /*@Transactional
    public List<Order> findOrders(OrderSearch orderSearch){

    }*/
}
// 단순히 A -> B 관계가 CASCADE 로 되어 있으면
// A 엔티티를 persist 할 때, B 엔티티도 연쇄해서 함께 persist

// CASCADE 옵션은 참조하는 주인이 private owner 일 경우에만 사용하는 것이 좋음
// delivery 는 order 에서만 참조하고 있음, orderItem 또한 order 에서만 참조하고 있음
// But, 만약 delivery 가 다른 엔티티에서도 참조된다면?
// CASCADE 옵션 사용하지 말고 별도의 Repository 만들어 사용하는 것이 좋음