package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    //Spring이 다 올라오고나면 @PostConstruct 가 호출되어 dBinit1() 이 호출되어 데이터가 들어간다.
    @PostConstruct
    public void init() {
        initService.dBinit1();
        initService.dBinit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;

        public void dBinit1() {
            Member member = createMember("userA", "서울", "강남구", "12345");
            em.persist(member);

            Book book1 = createBook("JPA1 BOOK", 10000, 100);
            em.persist(book1);

            Book book2 = createBook("JPA2 BOOK", 20000, 100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            Delivery delivery = createDelivery(member);

            Order order = Order.CreateOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }

        private Book createBook(String name, int price, int stockQuantity) {
            Book book1 = new Book();
            book1.setName(name);
            book1.setPrice(price);
            book1.setStockQuantity(stockQuantity); // "Ctrl + Alt + P"로 ()안의 숫자(100)을 stockQuantity 파라미터로 뽑아준다.
            return book1;
        }

        // "Ctrl + Alt + M"으로 선택된 영역 Method 분리 리팩토링(Extract Method)하여 생성 -> Accept Signature Change -> Replace
        private Member createMember(String name, String city, String street, String zipcode) { //변수명이 좀 이상하게 생겨서 "Shift + F6"로 수정
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }

        public void dBinit2() {
            Member member = createMember("userB", "경기", "과천시", "67890");
            em.persist(member);

            Book book1 = createBook("SPRING1 BOOK", 20000, 200);
            em.persist(book1);

            Book book2 = createBook("SPRING2 BOOK", 40000, 300);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

            Delivery delivery = createDelivery(member);

            Order order = Order.CreateOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }
    }
}
