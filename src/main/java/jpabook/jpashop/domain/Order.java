package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*; //원래 (fetch = FetchType.LAZY)인데 Alt+Enter로 이 문장을 import하여 (fetch = LAZY)로 변환

@Entity
@Table(name = "orders") //table 이름이 "orders"라고 알려주는 것. 그렇지않으면 관례로 class명인 Order를 table 이름으로 사용하여 "Order by"의 order와 겹침
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //다른 곳에서 Order Class의 직접 생성(new)을 막고, CreateOrder() 메서드를 통해서만 주문을 만들 수 있게 해준다.
                                                   //이렇게 제약을 걸어두는 방식으로 개발하면 좋은 설계와 유지보수를 할 수 있게 해준다.
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id") //id 에 mapping 할 table의 column 명을 "order_id"로 하겠다. DBA 들이 이런 방식을 선호한다.
    private Long id;

    @ManyToOne(fetch = LAZY) //order 여러건이 하나의 member와 관계
    @JoinColumn(name = "member_id")
    //Foreign Key(FK)에 "member_id"를 mapping 하겠다. 연관관계의 주인. 여기의 값이 바뀌면 다른 member로 변경됨. FK 변동
    private Member member;

    //@BatchSize(size = 1000) //컬렉션에서 default_batch_fetch_size를 세밀하게 적용
    @OneToMany(mappedBy = "order", cascade = ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; //주문시간, JAVA 8 에서는 hibernate에서 annotation mapping을 자동으로 지원해준다. 예전에 DateTime을 사용하면 annotation을 붙였어야 한다.

    @Enumerated(EnumType.STRING) //EnumType.ORDINAL 은 READY = 0, COMP = 1 의 형식으로 들어가서 중간에 다른 값이 추가되면 망한다.
    private OrderStatus status; //주문상태 [ORDER, CANCEL]

    /* 연관관계 메서드는 핵심적으로 컨트롤하는 쪽에 위치하는 것이 좋다 */
    /* == 연관관계 (편의) 메서드== 양방향 관계에서 있으면 편리하다 */
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    /* 연관관계 메서드는 실행코드에서 마지막 줄을 없앨 수 있게 해준다.
    public static void main(String[] args) {
       Member member = new Member();
       Order order = new Order();

       member.getOrders().add(order);
       order.SetMember(member); ==> 위의 연관관계 메서드를 작성하면 이 문장이 필요없어진다.
    }
     */

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //주문은 order만 생성해서 될 것이 아니라 orderItem, delivery 등 여러 연관관계가 들어가서 복잡해진다.
    //그래서 이런 복잡한 생성은 이렇게 별도의 생성 메서드가 있으면 좋다.
    //이런 스타일로 작성하는 것이 중요한 이유는, 앞으로 생성하는 지점을 변경해야할 때 이곳저곳 찾아다닐 필요없이 여기의 CreateOrder()만 변경하면 된다.
    //==생성 메서드==// 엔티티 분석에 의한 필드 생성
    public static Order CreateOrder(Member member, Delivery delivery, OrderItem... orderItems) { //static 메서드 확인...
        Order order = new Order();

        //Order 엔티티에 만들어 놓은 연관관계 편의 메서드를 사용하여 생성
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    //==비즈니스 로직==//
    /* 주문 취소 */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException();
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) { //orderItems = this.orderItems 인데, IntelliJ에서 색으로 구분을 해주어서 this를 빼도 이해된다.
            orderItem.cancel();
        }
    }

    //==조회 로직==/
    /* 전체 주문가격 조회 */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice(); //getTotalPrice() 메서드는 주문 시 주문가격과 주문수량이 있는 OrderItem 엔티티에 위치
        }

        return totalPrice;

        /* JAVA8: 위의 "for 문 + return 문"을 "Alt + Enter" -> "Replace with sum()" 으로 바꾸고 "Ctrl + Alt + N"으로 결과값을 합치면 아래와 같이 된다.
        return orderItems.stream()                  //stream 으로 바꾸어서
                .mapToInt(OrderItem::getTotalPrice) //mapToInt 로 바꾸어서
                .sum();                             //sum()을 한다.
        ***/
    }
}
