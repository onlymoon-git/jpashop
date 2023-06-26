package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("M") //default는 class name, 상속관계 mapping
@Getter @Setter
public class Movie extends Item {

    private String director;
    private String actor;
}
