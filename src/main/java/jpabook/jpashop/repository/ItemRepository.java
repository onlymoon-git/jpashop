package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {

        if (item.getId() == null) { //Id값이 없다라는 것은 완전히 새롭게 생성한 객체라는 것이다.
            em.persist(item); //item은 JPA에 저장하기 전까지는 Id가 없어서, 저장한다.
        } else {
            em.merge(item);   // 강제로 update와 유사
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
