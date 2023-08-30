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

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "delivery_id") // 연관관계의 주인 (일대일은 둘 중 아무 곳에 FK 지정 가능)
    private Delivery delivery; // 접근할 일이 많은 Order 의 delivery 를 FK 로 지정

    private LocalDateTime orderDate; // 주문 시간(Hibernate 가 날짜, 시간 자동 지원)

    @Enumerated(EnumType.STRING) // 꼭 붙여주자
    private OrderStatus status; // 주문 상태 [ORDER, CANCEL]
}
