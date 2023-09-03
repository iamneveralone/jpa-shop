package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 다른 곳에서 Order 를 직접 생성하면 안 되고, 다른 방식으로 생성해야 되는구나를 알려주는 역할
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY) // Order 입장에서는 다대일 (한 주문은 한 회원에게만 속함)
    @JoinColumn(name = "member_id") // 객체의 참조와 테이블의 외래 키를 매핑
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id") // 연관관계의 주인 (일대일은 둘 중 아무 곳에 FK 지정 가능)
    private Delivery delivery; // 접근할 일이 많은 Order 의 delivery 를 FK 로 지정

    private LocalDateTime orderDate; // 주문 시간(Hibernate 가 날짜, 시간 자동 지원)

    @Enumerated(EnumType.STRING) // 꼭 붙여주자
    private OrderStatus status; // 주문 상태 [ORDER, CANCEL]

    //== 연관관계 (편의) 메서드 ==// (양방향일 때 쓰면 좋음 -> 양쪽 세팅을 한 코드에서 설정)
    public void setMember(Member member) {
        this.member = member; // 현재 Order 객체의 member 필드에 member 객체 저장해주고,
        member.getOrders().add(this); // 해당 member 객체의 orders 에 현재 order 를 저장해줌
    }

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //== 생성 메서드 ==// (= 정적 팩토리 메서드)
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    } // 생성자와 동일한 역할 (생성자와 달리 이름을 줄 수 있어 메서드의 의도를 명확하게 표현 가능)

    //== 비즈니스 로직 ==//
    /*주문 취소*/
    public void cancel(){
        if (delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        } // ex) 고객이 한 번 주문할 때 2개 주문 가능 -> 2개의 orderItem 각각에도 cancel 필요
    }

    //== 조회 로직 ==//
    /*전체 주문 가격 조회*/
    public int getTotalPrice(){
        /*int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice = orderItem.getTotalPrice(); // 각 orderItem 의 total price 를 가져옴
        }
        return totalPrice;*/
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }
}
// "cascade 옵션"

// ex) 원래는 orderItems 컬렉션에 OrderItem 여러 개 먼저 저장(persist)하고, 그 다음에 Order 를 저장해야 했음
// 코드로 보면,
// persist(orderItemA)
// persist(orderItemB)
// persist(orderItemC)
// persist(order)

// -> cascade 옵션을 설정하게 되면?
// persist(order) 이 코드 하나만 실행하면 되는 것

// 마찬가지로, 원래대로라면 delivery 직접 persist 해주고, 그 다음에 order 도 직접 각각 persist 해줘야 했음
// cascade 옵션 사용하면, order 만 persist 해도 delivery 까지 같이 persist 호출됨

// @NoArgsConstructor(access = AccessLevel.PROTECTED) 사용 이유?
// @NoArgsConstructor 를 사용했다는 것은 즉 개발자가 파라미터를 받은 생성자를 임의로 만들었다는 뜻
// 우리는 현재 Order 클래스에 '생성 메서드'를 만들었고, 이 메서드를 사용해서 추후에 Order 객체 생성할 것임
// 그런데, 누군가는 new Order(); 형식을 통해 Order 객체를 생성하려는 시도를 할 가능성 있음
// 그렇게 되면, Order 객체를 생성하는 2가지 방법이 혼용되어 사용되면서 유지 보수 측면에서 좋지 않음
// -> 생성자의 접근 제어자를 protected 로 설정하면 다른 클래스에서 생성자 사용 불가
//    (new Order(); 작성 시 빨간 밑줄 나타나면서 컴파일 오류 발생)

// protected Order(){}
// <=> @NoArgsConstructor(access = AccessLevel.PROTECTED)
