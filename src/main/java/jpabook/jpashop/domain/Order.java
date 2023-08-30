package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
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
