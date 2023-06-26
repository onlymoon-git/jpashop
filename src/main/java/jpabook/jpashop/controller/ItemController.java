package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form) {

        Book book = Book.createBook(form.getName(), form.getPrice(), form.getStockQuantity(), form.getAuthor(), form.getIsbn());
        itemService.saveItem(book);
        return "redirect:/";
    }

    /*** 예제가 아래와 같이 제공되었는데, 위와 같이 수정함...
     @PostMapping("/items/new") public String create(BookForm form) {
     Book book = new Book();
     book.setName(form.getName());
     book.setPrice(form.getPrice());
     book.setStockQuantity(form.getStockQuantity());
     book.setAuthor(form.getAuthor());
     book.setIsbn(form.getIsbn());

     itemService.saveItem(book);
     return "redirect:/";
     }
     ***/

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("items/{itemId}/edit") //{itemId}에 @PathVariable("itemId")가 mapping 된다.
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) { //Form으로 갔다가 Form에서 수정하면 그 다음에 수정을 해야하니까 Form을 먼저 개발한다.

        Book item = (Book) itemService.findOne(itemId); //원래 Item one 에 넣어야하는데 예제를 간단히 하기위하여 casting하여 Book에다 넣는다.

        BookForm form = new BookForm();
        form.setId(item.getId()); //form을 update하는데 Book 엩티티가 아니라 BookForm 엔티티를 보낼 것이다.
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form); //이렇게 하면 form에 data가 넘어간다.

        return "items/updateItemForm";
    }

    /* 상품 수정 */
    @PostMapping("items/{itemId}/edit") //{itemId}에 @PathVariable("itemId")가 mapping 된다.
    public String updateItem(@ModelAttribute("form") BookForm form) {

        Book book = Book.createBook(form.getName(), form.getPrice(), form.getStockQuantity(), form.getAuthor(), form.getIsbn());

        book.setId(form.getId()); //createBook()에서 setting을 안해줘서... 이 문장이 없으면 계속 추가된다.

        itemService.saveItem(book);
        return "redirect:/items";

        /***
        Book book = new Book();
        book.setId(form.getId());
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());
        ***/

    }
}
