package jpabook.jpashop.domain.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //하나의 table에 다 때려넣는 전략
@DiscriminatorColumn(name = "dtype") //Book, Album, Movie에 따라서 처리할 수 있게
@Getter
@Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //== 비즈니스 로직 == stockQuantity를 가지고 있는 Item 엔티티에 비즈니스 로직이 있는 것이 응집도가 높다.//
    /* stockQuantity를 변경해야할 일이 있으면 @Setter를 열어서 바깥에서 계산해서 넣는 것이 아니라, stockQuantity기 있는 이 안에서 함수를 만들어서 처리하는 것이 좋다. 가장 객체지향적이다. */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
