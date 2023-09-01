package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 한 테이블에 다 때려박기
@DiscriminatorColumn(name = "dtype") // Single Table 이므로, DB 입장에서 저장할 때 구분할 수 있어야 함
@Getter @Setter
public abstract class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //== 비즈니스 로직 ==//

    // stock 증가
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
// addStock, removeStock 은 Item 도메인 스스로 해결할 수 있는 경우이므로, 도메인 안에서 처리하는 것도 좋은 방법임
// 수많은 로직들이 있는데, 서비스에 작성해야 할지 엔티티에 작성해야 할지?
// -> 엔티티 한 곳에서 처리가 가능하면 엔티티가 처리하면 됨 (데이터를 가지고 있는 쪽에 비즈니스 로직을 가지고 있는게 응집력 좋음)
//    But, 엔티티 하나에서 처리할 수 있는 범위를 넘어가면 서비스에서 협업이 필요!