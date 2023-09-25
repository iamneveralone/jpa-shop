package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookForm {

    // 상품 공통 특성
    private Long id; // 상품은 '수정' 기능이 있어서 id 값 필요

    private String name;
    private int price;
    private int stockQuantity;

    // Book 고유 특성
    private String author;
    private String isbn;
}
