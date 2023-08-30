package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("M") // @DiscriminatorColumn 에 대한 구분 값
@Getter @Setter
public class Movie extends Item {

    private String director;
    private String actor;
}
