package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; //주문 당시 가격
    private int count; //주문 수랑

    /* 다른 곳에서는 OrderItem을 생성하지 말아라(new), 아래의 createOrderItem() 메서드를 사용해라
    - Lombok "NoArgsConstructor(access = AccessLevel.PROTECTED)"에 의해서 아래 코드는 지울 수 있다.
    protected OrderItem() { //JPA를 쓰면서 protected를 사용하면 쓰지 말라는 것이다.

    }
    */

    //==생성 메서드==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) { //static 메서드임... 확인...
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    //==비즈니스 로직==//
    /* 주문 취소 */
    //재고 수량을 원복해주는 것이 cancel() 메서드의 역할이고, 원복을 하기위해서는 주문 수량을 알아야해서 OrderItem 엔티티에 존재
    public void cancel() { //Order 엔티티에도 이름이 같은 cancel()이 있다.
        getItem().addStock(count);
    }

    //==조회 로직==//
    /* 주문상품 전체 가격 조회 */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
