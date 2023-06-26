package jpabook.jpashop.repository.order.Query;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderQueryDto {
    private Long orderId;
    private String name; //LAZY 초기화 발생
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address; //고객주소가 아니라 배송지 정보, LAZY 초기화 발생
    private List<OrderItemQueryDto> orderItems;

    //컬렉션을 바로 넣을 수 없어서 컬렉션은 제거
    public OrderQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address/*, List<OrderItemQueryDto> orderItems*/) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
        //this.orderItems = orderItems;
    }
}
