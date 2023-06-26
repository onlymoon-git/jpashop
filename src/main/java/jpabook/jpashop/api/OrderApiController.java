package jpabook.jpashop.api;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.Query.OrderQueryDto;
import jpabook.jpashop.repository.order.Query.OrderQueryRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() { //이렇게 엔티티를 직접 노출하면 안된다.

        log.info("orderV1 실행...");
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) { //"iter + Tab"하면 자동으로 만들어 준다.
            order.getMember().getName();      //LAZY Loading 이어서 Proxy가 들어가 있었던 것을 강제 Loading
            order.getDelivery().getAddress(); //LAZY Loading 이어서 Proxy가 들어가 있었던 것을 강제 Loading

            //기존 내용에서 아래 내용이 추가된 것이다.
            //엔티티간의 관계에서 @JsonIgnore 가 제대로 반영되었는 지 확인 필요. Order 입장에서 OrderItem, Delivery, Member(?) 사이에,
            List<OrderItem> orderItems = order.getOrderItems(); //여기가 중요한 부분이다.
            orderItems.stream().forEach(o -> o.getItem().getName()); //주문(order)과 관련된 orderItem들을 다 가져와서 LAZY Loading인 item(명,...)을 강제로 Loading 해서 다 채워넣겠다(초기화하겠다).
            /*** 아래의 문장을 가독성이 좋은 위의 람다로 바꾸었다.
            for (OrderItem orderItem : orderItems) {
                orderItem.getItem().getName();
            } ***/
            /***  orderItems.stream().forEach(o -> o.getItem().getName()); 에 대한 부연설명
            getItem().getName()은 name 속성만을 가져오기 위함이 아닙니다.
            'fetch = LAZY'로 인해 프록시 객체가 들어있는 "Item객체"를 가져오기 위함입니다.
            getItem().getName()을 한다는 것의 의미는 "Item을 사용해야 한다는 것" 입니다.
            Item을 사용해야하니 그때 지연로딩이 활성화되어 Item객체를 가져오는 것입니다.
            name외에 다른 속성들도 조회가 가능한 것은 getName()을 사용해서 "Item 객체"를 조회했기 때문입니다.
            Item객체를 가지고 있으니 당연히 그 안의 속성들도 알 수가 있는 거죠.
            즉, 다시말해서.. 굳이 getName()이 아니라 다른 getXXX()를 사용해도 Item 객체를 조회해오는 사실은 변함이 없으니 똑같은 결과를 보실 수 있을 거예요.
            ***/
            /**
             * 스프링은 JSON을 만들때 Jackson이라는 라이브러리를 기본으로 사용합니다.
             * 이 라이브러리는 자바의 getXxx() 메서드를 호출해서 get을 때고 소문자로 만든 후, 필드값으로 사용합니다.
             * 따라서 getTotalPrice() -> totalPrice로 사용하는 것이지요.
             * 이런 방식을 자바 빈 프로퍼티 접근방식으라 합니다*
             */
        }

        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    //application.yml에 있는 default_batch_fetch_size 때문에 "N + 1"문제가 어느 정도 해결이 된다.
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit)
    {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit); //기존에 만들어 놓은 페이징에 영향을 주지않는 XToOne 관계에 해당하는 Member와 Deliver로 fetch join하는 메서드
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    @Getter //@Getter/@Setter가 없으면 "no properties ~~~" error가 발생
    //@Data
    static class OrderDto { //DTO를 반환할 때에는 DTO안에 엔티티가 있으면 안된다. 이 경우 OrderItem 엔티티가 외부에 노출이 되었다.
        private Long orderId;
        private String name; //LAZY 초기화 발생
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; //고객주소가 아니라 배송지 정보, LAZY 초기화 발생

        //private List<OrderItem> orderItems; //여기서 이렇게 엔티티를 반환하면 안된다. DTO로 변환해서 반환해야 한다.
        private List<OrderItemDto> orderItems; //DTO로 변환해서 반환해야 한다. 컬렉션을 사용하면 query가 너무 많이 나간다. 최적화에 대한 고민 필요.

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();

            //order.getOrderItems().stream().forEach(o -> o.getItem().getName()); //초기화라고 표현
            //orderItems = order.getOrderItems(); //OrderItem은 엔티티라서 REST API에서 결과값이 나오지 않는다. 그래서 위의 내용 추가.
            //위의 두줄을 DTO로 변환하는 코드로 변환
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }

}
