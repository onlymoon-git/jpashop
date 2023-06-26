package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    /* 실무상으로는 多對多 관계가 필요없지만 한번 해보기위해서 넣은 관계이다 */
    /* Category도 List로 Item을 가지고, Item도 List로 Category를 가진다 */
    @ManyToMany
    // 객체는 컬렉션과 컬렉션이 있어서 多對多 관계가 가능한데, 관계형 DB는 컬렉션 관계를 양쪽에 가질수 없기때문에 一對多, 多對一 로 풀어내는 중간테이블이 있어야 한다.
    @JoinTable(name = "category_item",   // 중간테이블을 mapping 해줘야 한다.
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    /* 연관관계 편의 메서드 */
    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }
}
