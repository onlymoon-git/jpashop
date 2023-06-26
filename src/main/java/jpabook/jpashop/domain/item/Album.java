package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("A") //default는 class name, 상속관계 mapping
@Getter @Setter
public class Album extends Item {

    private String artist;
    private String etc;
}
