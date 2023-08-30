package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent; // 계층형 카테고리 ex) 쇼핑몰

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();
}
// 다대다 경우에는 @JoinTable 을 사용하여 중간 테이블을 매핑해줘야 함
// (참고) 실무에서는 @ManyToMany 사용하지 말자
// -> @ManyToMany 는 편리한 것 같지만, 중간 테이블(CATEGORY_ITEM)에 컬러 추가 불가능
//    또한, 세밀하게 쿼리를 실행하기 어렵기 때문에 실무에서 사용하기에 한계가 있음
// -> 중간 엔티티(CategoryItem)을 만들고, @ManyToOne, @OneToMany 로 매핑해서 사용하자!

// "계층형 카테고리" (쇼핑몰 예시)
// 전체 - 상의 - 아우터, 티셔츠, 맨투맨
//     - 하의 - 청바지, 슬랙스, 반바지

// 중간 카테고리인 '상의'를 기준으로 부모는 전체 하나(1), 자식은 아우터, 티셔츠, 맨투맨 3개(N)
// -> 즉, 카테고리 입장에서 일반화하면 '계층형 카테고리에서 하나의 카테고리에게 부모는 1개까지 존재 가능, 자식은 N개 존재 가능'
// 만약, '전체' 카테고리를 조회했다면, parent 는 null, child 는 상의, 하의