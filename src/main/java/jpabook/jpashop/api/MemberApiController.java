package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController //@Controller + @ResponseBody(데이터 자체를 바로 JSON 이나 XML로 보낼때 사용) : 두개의 기능을 합한 Spring에서 제공하는 annotation
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    //이렇게 엔티티를 직접 return하면 엔티티의 모든 정보가 노출이 된다. 이렇게 엔티티를 직겁 반환하면 안된다.
    @GetMapping("/api/v1/members")
    public List<Member> memberV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result memberV2() { //엔티티를 DTO로 변환하는 수고가 추가되었지만, 이렇게 해야한다. 엔티티가 변경되어도 API 스펙이 변하지않는 장점이 생긴다.

        List<Member> findMembers = memberService.findMembers(); //아래줄에서 List<Member>를 List<MemberDto>로 바꾼다.
        List<MemberDto> collect = findMembers.stream() //stream을 돌린 다음에 map을 해가지고 memberDto로 바꿔주면 된다.
                .map(m -> new MemberDto(m.getName())) //member 엔티티 m.getName()으로 이름을 꺼내와서 Dto로 넣어 바꿔친다.
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    //Result라는 껍데기를 씌워서 data 필드의 값이 List로 나갈 것이다. 이런 식으로 한번 감싸지않고 List 타입의 collection으로 바로 내보내면 처음부터 JSON 배열타입으로 나가기때문에 유연성이 떨어진다.
    /*{
          "count": 3,
          "data": [
              {
                  "name": "new-hello"
              },
              { ...
    */
    static class Result<T> {
        private int count;
        private T data;
    }

    //API 스펙에 맞는 DTO를 항상 생성해라.
    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name; //노출할 것만 노출된다. 따라서 API 스펙과 DTO가 1:1이 된다.
    }

    //V1의 유일한 장점은 CreateMemberRequest라는 class를 안만든다는 것이다.
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) { //이런 식으로 엔티티(Member)를 외부에 노출시키는 것은 나쁜 방법이다.
                                                                                  // API를 만들 때는 엔티티를 파라미터로 받으면 안되고, 외부에 노출해서도 안된다.
                                             //@RequestBody 하면, JSON으로 온 Body를 Member에 그대로 mapping해서 넣어준다. JSON 데이터를 Member로 바꿔주는 것.
                                             //@Valid 하면 member 객체 안에 있는 필드에 대해 javax validation 을 자동으로 해준다.
        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    //response 와 request 를 모두 별도의 객체로 만들었다.(DTO 생성)
    //V2의 장점은 엔티티가 바뀌더라도 API 스펙은 바뀌지 않는다.
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) { //DTO를 사용함으로서 엔티티와 프리젠테이션 영역에 대한 로직을 분리할 수 있다.
                                           //JSON으로 요청 온 값을 @RequestBody로 CreateMemberRequest에 binding 한다.
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    //PutMapping으로 수정을 하면 같은 요청을 여러번 보내도 나중의 요청에 의해서는 변경되지 않는다.
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2( //수정 DTO는 등록 DTO에 비해서 제한적이기때문에 별도로 만든다.
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());

        //id를 넘겨주기위해서 findOne으로 쿼리를 했다. 위 문장에서 member를 return 받아서 id를 넘기도록 처리를 해도 되나, 쿼리가 무겁지 않으면 커맨드(update)와 쿼리(findOne)을 분리하여 처리한다.
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    //이 class 안에서만 사용하는 DTO라서 이 안에 놓는다.
    //DTO의 장점: Member 객체 중에서 어떠한 값이 넘어오는 지 API 문서를 보지않아도 알 수 있다. 여기서는 name만 넘어온다는 것을 알 수 있다.
    //엔티티에는 최대한 자제해서 @Getter annotation만 사용하는데, DTO에는 lombok annotation을 많이 사용한다.
    @Data
    static class UpdateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty //엔티티에 넣지 않고 여기서 넣어주면 된다. 엔티티에 넣으면 name이 빈 값이 들어오는 업무를 만들 수 없게된다.
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) { //@AllArgsConstructor 로 생성하면 된다.
            this.id = id;
        }
    }
}
