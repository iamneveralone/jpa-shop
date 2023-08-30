package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded // @Embeddable, @Embedded 둘 중 하나만 명시해도 되지만, 둘 다 명시하는게 알아보기 편함
    private Address address;

    // Member 입장에서는 일대다 (한 회원이 여러 개 주문 가능)
    // mappedBy 옵션을 통해 Order 테이블에 있는 member 필드에 의해 매핑됨을 명시!
    // orders 는 연관관계의 주인이 아니라 '연관관계의 거울' (단지 읽기만 가능)
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
// Member 와 Order 는 양방향 참조
// 문제는, DB 에 FK 는 orders 의 member_id 하나밖에 없음
// 만약, Member 와 Order 의 관계를 바꾸고 싶으면 FK 의 값을 변경해야 함
// (Member 에도 Order 를 list 로 갖고 있는 orders 라는 필드가 존재하고,
// Order 에도 Member 라는 필드를 가지고 있음)
// -> JPA 는 그러면 둘 중에서 어디의 값이 변경되었을 때 FK 값을 바꿔야 하지? 라는 혼란 발생
// ex) Order 의 member 에는 값을 세팅했는데, Member 의  orders 에는 값을 세팅하지 않은 경우
// -> 이런 경우에 JPA 는 둘 중에 뭘 믿고 FK 값을 변경 or 유지해야 하지?

// 객체는 변경 포인트가 2개, but 테이블은 FK 가 한 군데에만 존재
// Member 의 orders 또는 Order 의 member 중에 얘 값이 변경되었을 때 FK 값을 바꿀거야!
// -> 이게 바로 "연관관계의 주인" (FK 를 가지고 있는 테이블)