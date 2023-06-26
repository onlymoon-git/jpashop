package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    //기능 - 주문, 취소, 검색

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /* 주문 */
    @Transactional //저장을 해야하기 때문에
    public Long order(Long member_id, Long item_id, int count) { //화면 UI 에서 넘어오는 값들

        //먼저 엔티티들 조회
        Member member = memberRepository.findOne(member_id);
        Item item = itemRepository.findOne(item_id);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count); //OrderItem의 O가 대문자(참조변수를 사용하는 것이 아니라서 ???), static 메서드 확인 ...

        //주문 생성
        Order order = Order.CreateOrder(member, delivery, orderItem); //원래는 위의 delivery, orderItem 둘 모두 JPA에 값을 넣어준 다음에 여기에서 setting 홰야 한다.
                                                                      //그런데 아래에서 orderRepository.save(order) 한곳에서만 JPA에 값을 넣어줬다.
                                                                      //그 이유는 Order Class에서 cascade = CascadeType.ALL 때문에 Order를 persist 할 때 함께 persist 해서이다.
        // 아래 문장을 "Ctlr + Alt + V"로 지역변수로 refactoring
        //Order.CreateOrder(member, delivery, orderItem)

        //주문 저장
        orderRepository.save(order); //Order Class에서 cascade = ALL 이 붙은 필드도 함께 persist 한다. persist 하는 lifecycle이 동일하기때문에 가능. 잘 모르겠으면 별도로 persist 해라...
                                     // delivery와 orderItem은 Order에서만 참조하기때문에 가능. private 이기도 하다.
        return order.getId();
    }

    /* 주문 취소 */
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);

        //주문 취소
        order.cancel();
        /* JPA의 진짜 강점 */
        /* SQL을 직접 사용하면, 주문 취소를 하게되면 order.cancel()에서 변경된 DeliverStatus, OrderStatus 에 대해 update query를 repository에 직접 날려야한다.(order table에 update)
           또 Order에서 call한 OrderItem.cancel() 에서 재고 더해주는 부분도 update를 직접 해야한다.(Item table에 update)
           이 update들을 order.cancel()을 한 다음에 전부 coding 해서 넣어야 한다.
           JPA에서는 엔티티 안에 있는 데이터만 바꿔주면 JPA가 알아서 바뀐 변경 포인트들에 dirty-checking(변경내역감지)이 일어나면서 변경내역을 다 찾아서 DB에 update query가 다 날라간다.
        */
    }

    //검색
    public List<Order> findOrders(OrderSearch orderSearch) { //이런 단순한 기능들은 Controller에서 바로 Repository를 불러도 된다.(Service를 거치지 안고)
        return orderRepository.findAllByString(orderSearch);
    }
}
