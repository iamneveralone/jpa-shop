package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
// 값 타입은 변경 불가능하게 설계해야 함!
// @Setter 를 제거하고, 생성자에서 모두 초기화해서 변경 불가능한 클래스를 만들자
// JPA 스펙상 엔티티나 임베디드 타입은 자바 기본 생성자를 public 또는 protected 로 설정해야 함
// (public 보다는 protected 가 그나마 더 안전!)

// 만약 회원의 주소를 변경할 경우에는?
// -> Member 클래스에서 Address 를 사용하려면 완전히 새로운 Address 객체를 만들어서 통으로 변경해주면 됨