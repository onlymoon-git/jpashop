package jpabook.jpashop.repository.order.Query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

/* Repository Directory에 별도로 order/Query Directory를 만든 이유 -> 관심사 분리
   - Repository/OrderRepository Class는 Order 엔티티를 조회하는 용도로 사용. 엔티티를 찾을 때는 이 Class 사용
   - Repository/order/~Query Directory는 화면이나 API에 의존관계가 있는 엔티티가 아닌 조회를 분리하려고 사용.
 */

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders(); //query 1번 -> 결과 N개
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); //위 결과 N개에 의해서 query N번 -> 결국 "N + 1"의 문제가 발생
            o.setOrderItems(orderItems);
        });

        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.Query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.Query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" + //컬렉션을 바로 넣을 수 없어서 컬렉션은 제거
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }
}
