package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor
    /* 원래 EntityManager 는 @Autowired로는 Injection이 안되고 @PersistenceContext라는 표준 annotation이 있어야 Injection이 된다.
       그러나 스프링이, 정확히는 Spring Data JPA가, @Autowired를 사용해도 자동 주입이 되게 지원하여 아래 소스도 가능.
       아직은 안되는데 나중에 스프링이 이 기능을 제공해 줄 예정이라고 함
       JPA 활용 1평 - "회원서비스 개발"의 끝부분 설명 참조
     */
public class MemberRepository {

    //@Autowired
    private final EntityManager em; //JPA를 사용하기때문에 EntityManager 가 필요하다.

    //생성자 생략. @RequiredArgsConstructor 에 의해 final이 붙은 참조변수에 대해 생성자가 자동 생성이 된다.
    //public MemberRepository(EntityManager em) {
    //    this.em = em;
    //}

/*
public class MemberRepository {

    @PersistenceContext //스프링부트가 이 annotation 이 있으면  MemberRepository의 생성자에서 EntityManager를 주입해준디.
    private EntityManager em; //JPA를 사용하기때문에 EntityManager 가 필요하다.
    //위 2줄이 "private final EntityManager em;"의 한줄로 변경
*/

    public Long save(Member member) {
        em.persist(member); //저장. persist를 하면 @GeneratedValue 에서 생성된 PK값이 들어가는 것이 보장된다. 왜냐하면 영속성컨텍스트에서는 key, value가 들어가는데 PK가 key
        return member.getId();
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id); //조회
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();

        /* 아래 문장이 "Ctrl + Alt + N"으로 위처럼 바뀜. createQuery 로 Member 를 List 로 만든 result를 한 줄로 만든다.
        List<Member> result = em.createQuery("select m from Member m", Member.class)
                .getResultList();
        return result; cursor가 result 위에 있는 상태에서 "Ctrl + Alt + N"을 눌러야 함
        */
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}

//수정: member.setName("변경할 이름")
//삭제: em.remove(member)

// JPA에서 EntityManager는 데이터베이스와의 연결을 관리하며, 영속성 컨텍스트(Persistence Context)를 유지합니다.
// 영속성 컨텍스트는 엔티티 인스턴스를 저장하고 관리하며, 엔티티와 데이터베이스 간의 상호작용을 추적합니다.
// 영속성 컨텍스트(Persistence Context)는 엔티티(Entity) 인스턴스를 관리하는 환경을 말합니다.
// 영속성 컨텍스트는 엔티티 매니저(Entity Manager)를 통해 생성되며, 엔티티 매니저는 영속성 컨텍스트와 관련된 모든 엔티티의 생명주기를 관리합니다.