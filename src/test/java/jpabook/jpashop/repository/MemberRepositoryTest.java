package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class) //JUnit한테 스프링과 관련된 테스트를 한다고 알려주는 것
@SpringBootTest
//public class MemberRepositoryTest { //JUnit5 에서는 public을 지워야 정상 작동 ?
class MemberRepositoryTest {

    @Autowired //의존관계 필드주입 - Test라서 사용
    MemberRepository memberRepository;

    @Test
    // Transactional annotation이 Test에 있으면 Test가 끝나고 rollback을 한다. Test에서 data를 보고싶으로 @Rollback(false)를 하면 된다.
    @Transactional //이게 없으면 No EntityManager ~ 오류 발생. EntityManager 를 통한 모든 data 변경은 항상 transaction 안에서 이루어져야 한다.
    @Rollback(value = false)
    public void testMember() throws Exception {
        //tdd + TAB
        //given
        Member member = new Member();
        member.setName("memberA");

        //when
        Long saveId = memberRepository.save(member);
        Member findMember = memberRepository.findOne(saveId);

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getName()).isEqualTo(member.getName());

        // 영속성 컨텍스트 안에서는 id값(식별자값)이 같으면 같은 Entity로 인식한다.
        // 영속성 컨텍스트에서 Entity가 관리되고 있어서 객체를 다시 생성하면 기존에 관리되고 있는 것이 나온다(1차캐시)
        // 따라서, 스프링 로그를 보면 insert만 있고, 1차캐시에서 findMemeber를 가져와서 select는 없다.
        Assertions.assertThat(findMember).isEqualTo(member);

        System.out.println("findMember.getname() = " + findMember.getName());
    }
}

// JUnit Vintage은 JUnit 4와 이전 버전의 테스트를 실행하기 위한 모듈
// JUnit 5에서는 Vintage 모듈을 사용하려면 별도의 의존성을 추가해야