package jpabook.jpashop.repository.order.simpleQuery;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

/* DTO가 Controller에 있으면 의존관계가 Repository가 Controller를 보는 이상한 상태가 벌어질 수 있어서 여기에 다시 Class를 만들었다. */
/* 의존관계는 "Controller -> Service -> Repository" 이거나 "Controller -> Repository" 등 한방향으로 흘러가야 한다. */
@Data
public class OrderSimpleQueryDto {
    private Long orderId;
    private String name; //LAZY 초기화 발생
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address; //고객주소가 아니라 배송지 정보, LAZY 초기화 발생

    /* DTO는 이렇게 엔티티를 파라미터로 받는 것은 크게 문제가 되지 않는다. 중요하지않은 곳에서 중요한 엔티티에 의존하는 것이어서 그렇다. */
    //엔티티를 파라미터로 받는 것에서 변경
    //public OrderSimpleQueryDto(Order order) {
    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}
