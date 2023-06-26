package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//JPA의 모든 데이터 변경이나 로직들은 transaction 안에서 이루어져야 한다. 따라서 이 annotation 이 필요. 그래야 LAZY loading 등이 된다.
@Transactional(readOnly = true) //읽기전용 메서드에 readOnly = true 로 하면 좀 더 성능 최적화를 한다. dirty checking 제외 등
               // class level 에서 쓰면 public 메서드들에는 transaction 이 걸려서 들어간다.
               // javax에서 제공하는 것과 spring이 제공하는 것이 있는데 spring이 제공하는 것을 사용해야 spring에 제공하는 많은 option을 쓸 수 있다.
@RequiredArgsConstructor //final이 붙은 멥버변수에 대해서 생성자를 자동으로 만들어준다.
//@AllArgsConstructor //Lombok, 생성자를 자동으로 만들어준다.
public class MemberService {

    private final MemberRepository memberRepository;

    /*
    @Autowired //생성자가 한개이면 생략해도 주입이 된다.
    public MemberService(MemberRepository memberRepository) { // @AllArgsConstructor 에 의해서 자동으로 만들어진다.
        this.memberRepository = memberRepository;
    }
    */

    //회원 가입
    @Transactional //변경이 가능해야 해서, class level에 걸려있는 "readOnly = true"를 끄기 위해서 붙여줌
    public Long join(Member member) {

        validateDuplicateMember(member); //중복회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) { //validateDuplicateMember 함수 위에서 "Alt + Enter"로 이 class 안에 메서드 생성

        /* 실제로는 아래처럼 복잡하게 할 필요는 없고, 멤버 수를 세어서 0보다 크면 에러처리 하면 된다. */
        /* 두명이 동시에 "memberA"라는 name으로 등록을 할 수가 있어서, 최후로 member의 name을 DB에서 Unique 제약조건으로 잡는 것이 좋다 */
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다. !!!"); //메소드가 호출되기 전에 객체의 상태나 환경이 적절하지 않을 때 발생하는 예외
        }
    }

    //회원 전체 조회
    @Transactional(readOnly = true) //읽기전용 메서드에 readOnly = true 로 하면 좀 더 성능 최적화를 한다. dirty checking 제외 등
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    //회원 한건 조회
    @Transactional(readOnly = true) //읽기전용 메서드에 readOnly = true 로 하면 좀 더 성능 최적화를 한다.
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id); //@Transactional annotation이 있는 상태에서 조회하면 영속성 컨텍스트에서 가져온다. 없으면 DB에서 가져와서 영속 상태가 된다.
        member.setName(name); //변경감지에 의해서 변경이 된다.
    }

}
