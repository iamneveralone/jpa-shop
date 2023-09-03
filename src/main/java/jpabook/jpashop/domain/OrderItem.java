package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; // 주문 당시의 가격 (상품 가격은 변할 수 있기 때문)
    private int count; // 주문 수량

    //== 생성 메서드 ==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }
    // 생성 메서드에서 setOrder 를 하지 않은 이유?
    // 비즈니스 로직상 Order 를 만들기 위해서 OrderItem 이 필요
    // OrderItem 을 먼저 생성해준 다음 Order 를 생성할 때 이전에 생성한 OrderItem 을 넘기게 되는데
    // order.createOrder() -> (내부) order.addOrderItem() -> (내부) orderItem.setOrder()
    // -> 따라서, OrderItem 의 내부에서는 따로 setOrder() 필요X

    //== 비즈니스 로직 ==//
    public void cancel() {
        getItem().addStock(count); // 재고 원상 복구
    }

    //== 조회 로직 ==//
    /*주문 상품 전체 가격 조회*/
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
