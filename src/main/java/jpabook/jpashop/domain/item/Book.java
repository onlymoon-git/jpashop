package jpabook.jpashop.domain.item;

import lombok.Getter;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B") //default는 class name, 상속관계 mapping
@Getter //@Setter 삭제
public class Book extends Item {

    private String author;
    private String isbn;

    /* 내가 임의로 개발한 내용 */
    //==생성 메서드==//
    public static Book createBook(String name, int price, int stockQuantity, String author, String isbn) { //static을 붙여야...

        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        book.author = author;
        book.isbn = isbn;

        return book;
    }
}
