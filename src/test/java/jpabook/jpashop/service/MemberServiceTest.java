package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

//순수한 단위테스트를 하는 것이 아니라, 스프링과 integration해서 JPA와 DB까지 테스트를 하기위하여 아래 2개의 annotation 사용.
@RunWith(SpringRunner.class) //JUnit 실행할 때 스프링과 함께 실행하겠다.
@SpringBootTest //SpringBoot를 띄운 상태에서 테스트하겠다. 이것이 없으면 아래의 @Autowired는 모두 실패한다.
@Transactional //data를 변경하기 위하여. 이 annotation이 TEST에 있으면 기본적으로 rollback이 된다.
public class MemberServiceTest {

    //테스트라서 필드주입을 사용
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    //@Autowired EntityManager em;

    @Test
    /* 데이터를 rollback 하지않고 insert문을 볼 수 있는 2가지 방법
       1. @Rollback(false)
       2. @Autowired EntityManager em;
          후에 then 시작에 em.flush(); 영속성컨텍스트에 있는 객체가 query로 DB에 반영된다.
     */
    //@Rollback(value = false) //transaction commit이 되어야 insert문이 나가는데 @Transactional은 기본적으로 commit 되지않고 rollback을 시켜서 insert문이 안나간다. false로 하면 insert가 보인다.
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long saveId = memberService.join(member);

        //then
        //em.flush(); //영속성컨텍스에 있는 변경이나 등록내용을 DB에 반영하는 메서드이다. 테스트에서는 @Transactional에 의해서 다시 rollback 된다.
        /* Assert.assertEquals() 에서 "Alt + Enter"로 assertEquals()로 간단히 변경 */
        assertEquals(member, memberRepository.findOne(saveId));
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예회() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        memberService.join(member2); //예외가 발생해야 한다. (expected = IllegalStateException.class)에 의해서 아래의 try ~ catch 처럼 처리된다.

        /* (expected = IllegalStateException.class) 가 없으면 아래와 같이 처리해야 한다.
        try {
            memberService.join(member2); //예외가 발생해야 한다.
        } catch (IllegalStateException e) { //메소드가 호출되기 전에 객체의 상태나 환경이 적절하지 않을 때 발생하는 예외
            System.out.println("member2 에서 입력 실패 !!!");
            return;
        }
        */

        //then
        fail("예외가 발생해야 한다. !!!"); //여기에 오면 안되는데 왔다는 것을 표시하기 위하여. "Assert.fail"을 "Alt + Enter"하여 변경
    }
}