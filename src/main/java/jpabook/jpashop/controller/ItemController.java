package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model){ // URL 접속 시 폼을 화면에 띄워주는 용도
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }
    // Model 객체는 Controller 에서 생성된 데이터를 View 로 전달할 때 사용하는 객체
    // addAttribute(String name, Object value) : value 객체를 name 이름으로 추가

    @PostMapping("items/new") // 폼에 내용을 채워서 POST
    public String create(BookForm form){

        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/items"; // 저장된 책 목록으로 바로 갈 것임
    }

    @GetMapping("/items")
    public String list(Model model){
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
        Book item = (Book) itemService.findOne(itemId); // Id 에 해당하는 Book 객체를 가져옴

        BookForm form = new BookForm(); // 엔티티(Book)를 보내는 것이 아니라 폼(BookForm)을 보낼 것임
        // 가져온 Book 객체의 정보를 BookForm 에 옮겨담음
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") BookForm form){

        // 수정된 정보가 담긴 BookForm 정보를 새로운 Book 객체에 옮겨담음
        // 여기서 book 은 준영속 엔티티 (영속성 컨텍스트가 더는 관리X) -> JPA 가 관리하지 않음
        /*Book book = new Book();
        book.setId(form.getId()); // new 로 생성하기 했지만, id 를 갖게 됨(이전에 JPA 에 의해 DB 에 한 번 저장되었던 걸 불러온 느낌) -> 준영속 엔티티
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());
        itemService.saveItem(book);*/ // 타고 들어가면 itemRepository 의 save 메서드에서 기존에 DB 에 해당 item 이 존재하는 경우에 em.merge(item) 실행

        // 더 좋은 방법
        // (1) 컨트롤러에서 어설프게 엔티티 생성하지 말자
        // (2) 트랜잭션이 있는 서비스 계층에 식별자(id)와 변경할 데이터를 명확하게 전달하자(파라미터 or dto)
        // (3) 트랜잭션이 있는 서비스 계층에서 영속 상태의 엔티티를 조회하고, 엔티티의 데이터를 직접 변경하자
        // (4) 트랜잭션 커밋 시점에 변경 감지가 실행된다
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());

        return "redirect:/items";
    }
}
// 동작 흐름
// 1. /items/new 로 GET 요청이 들어오면, items/createItemForm.html 을 화면에 띄워줌
// createItemForm.html 에는 다음과 같은 코드 존재
// -> <form th:action="@{/items/new}" th:object="${form}" method="post">
// -> <input type="text" th:field="*{name}" class="form-control" placeholder="이름을 입력하세요">
// -> <input type="number" th:field="*{price}" class="form-control" placeholder="가격을 입력하세요">
// -> <input type="number" th:field="*{stockQuantity}" class="form-control" placeholder="수량을 입력하세요">
// -> <input type="text" th:field="*{author}" class="form-control" placeholder="저자를 입력하세요">
// -> <input type="text" th:field="*{isbn}" class="form-control" placeholder="ISBN을 입력하세요">
// 2. createItemForm.html 에서 form 태그 영역 내의 input 태그를 통해 각 속성값 입력받고, submit 버튼 눌러 /items/new 로 POST 요청
// 3. 즉, GET 요청을 보냈을 때 model 에 넘겼던 빈 BookForm 객체의 속성값들이 채워진 것이다.
// 4. 새로운 Book 객체를 생성해 BookForm 에 담긴 속성값들을 옮겨담는다.

// createForm 메서드에서 왜 Model 에 빈 객체 생성해서 넘길까?
// -> new BookForm()을 통해 form 을 초기화하기 위해서!!

// createItemForm.html 에는 <form th:action="@{/items/new}" th:object="${form}" method="post"> 코드 존재
// -> 'form' 이라는 이름으로 모델에 저장된 객체를 폼의 데이터 바인딩 대상으로 사용한다는 의미!
// 만약 'form' 객체를 초기화하지 않는다면 다른 방식으로라도 'form' 객체를 초기화해야함
// 그렇지 않으면 'form' 객체가 없어 폼의 데이터 바인딩이 실패하거나 예외 발생 가능

// 회원 가입을 하거나 어떤 물건을 새로 등록할 때, 기존의 정보는 아무것도 입력되지 않은 상태여야 함
// -> 이 때는 빈 객체를 생성하여 넘김 ex) model.addAttribute("form", new BookForm());
// 반대로, 어떤 정보를 수정하고자 할 때는 기존의 정보가 화면에 출력된 상태여야 함
// -> 기존 데이터들을 담고 있는 객체를 넘김 ex) model.addAttribute("items", items);