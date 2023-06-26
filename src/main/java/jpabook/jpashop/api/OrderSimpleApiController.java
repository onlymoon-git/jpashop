package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simpleQuery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simpleQuery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/***
 * xToOne관계(ManyToOne, OneToOne)에서 어떻게 성능 최적화하는 지를 보여줄 것임.
 * Order를 조회하고
 * Order에서 -> Member와 연관이 걸리고
 * Order와 -> Delivery가 연관이 걸리게 할 것이다.
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /*** 이 예제를 완성시키려먼 엔티티도 수정해야하고, Hibernate5Module도 설치해야해서 미완성으로 놔둔다 ***/
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        return all;
    }

    /* V2의 문제점: LAZY Loading으로 인한 Database query가 너무 많이 호출된다.
    * Order, Member, Delivery 3개의 table을 건드려야 하는데  */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2() { //List로 반환하면 안되고, Result로 감싸야 한다. MemberApiController의 @GetMapping("/api/v1/simple-orders") 참조

        //ORDER가 2개 있으면
        //"N + 1"문제 발생: ORDER query 1개 + 회원 query 1개 + 배송 query 1개 발생하여 "1 + N" 개의 query가 발생한다.
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());  //1. oreders를 가져와서
        List<SimpleOrderDto> result = orders.stream()                             //2. stream으로 돌려서
                .map(o -> new SimpleOrderDto(o))                                  //3. map으로 해서 (A를 B로 바꾸는 것: order를 SimpleOrderDto로 바꾸는 것)
                .collect(Collectors.toList());                                    //4. colliect로 해서 다시 List로 변환한다.

        return result;
    }

    //V2와 V3는 결과적으로 동일한데 Query만 다르다.
    //V3의 단점은 엔티티를 찍어서 전부 조회하는 것이다. ???
    //나는 V3를 사용해야 겠다. V4 보다도...
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3() { //List로 반환하면 안되고, Result로 감싸야 한다. MemberApiController의 @GetMapping("/api/v1/simple-orders") 참조
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    //V3는 엔티티를 조회해서 DTO로 변환을 했지만, V4는 바로 JPA에서 DTO로 끄집어 내는 방식이다. 성능 최적화를 더 할 수 있다.
    //V3와 V4는 우열을 가리기 어렵다. V4는 재사용성이 없다.
    //V3는 엔티티를 조회한 것이고, V4는 DTO를 조회한 것이고 엔티티가 아니기때문에 변경할 수 없다.
    //V4는 code상 지저분하다.
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name; //LAZY 초기화 발생
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; //고객주소가 아니라 배송지 정보, LAZY 초기화 발생

        /* DTO는 이렇게 엔티티를 파라미터로 받는 것은 크게 문제가 되지 않는다. 중요하지않은 곳에서 중요한 엔티티에 의존하는 것이어서 그렇다. */
        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }
}
