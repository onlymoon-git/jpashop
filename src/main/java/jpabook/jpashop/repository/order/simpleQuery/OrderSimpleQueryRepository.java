package jpabook.jpashop.repository.order.simpleQuery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    //이 엔티티는 화면/API에 국한된 repository 이기때문에 OrderRepository와 분리했다.
    //JPA는 기본적으로 엔티티와 VO(embeded)만 반환할 수 있다. DTO같은 것은 안된다. 하려면 new operation을 꼭 써야한다.
    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                        //아래에서 "new OrderSimpleQueryDto(o)"와 같이 엔티티를 넘기면 식별자로 넘어가서 안된다. 따라서 생성자가 엔티티를 받을 수 없다. 그래서 파라미터를 일일이 써줘야 한다.
                        //API 스펙이 여기에 들어와 있는 것이라 좋지 않다. 물리적으로는 계층이 나우어져 있지만 논리적으로는 계층이 깨진 것이다. repository가 바뀌면 여기도 바뀌어야 한다.
                        "select new jpabook.jpashop.repository.order.simpleQuery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
}
