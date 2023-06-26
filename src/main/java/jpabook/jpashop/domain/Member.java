package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id //Primary Key로 사용
    @GeneratedValue //sequence 값 사용
    @Column(name = "member_id") //JPA에서 @Column은 엔티티 클래스의 속성을 데이터베이스 테이블의 열(column)과 매핑할 때 사용
    private Long id;

    //@NotEmpty //@Valid 에 의해서 비어있는 지 체크. 여기에서 체크하지말고 DTO에서 해라...
    private String name;

    @Embedded //내장 Type을 포함했다는 annotation, 원래는 Embeddable 과 Embedded 둘중의 하나만 있어도 된다. 보통 Embedded를 사용
    private Address address;

    //하나의 Member에 대하여 주문이 여러건이라서 @OneToMany를 붙임
    //@JsonIgnore //Json으로 변환할 때 빼라. 엔티티에 프레젠테이션에 관련되 로직이 들어간 것이다. 이렇게 하면 안된다. 양방향이 걸리는 곳에서는 JsonIgnore 를 모두 걸어 관계를 끊어주어야 한다.
    @OneToMany(mappedBy = "member") //mappedBy(연관관계의 거울) = "member"의 의미는 Member table의 변경에 의해서만 변경되는 필드
                                    // 따라서 이 필드의 값을 변경시킨다고해서 Order table의 FK값이 변경되지 않는다.
    private List<Order> orders = new ArrayList<>();

    /* lombok 의 @Getter @Setter 를 사용해서 필요없음
    public Long getId() {
        return id;
    }
    ...
    */
}