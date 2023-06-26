package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/* @SpringBootApplication annotation 이 붙은 Class(JpashopApplication)의 package(jpabook.jpashoop) 를
   포함한 하위는 모두 ComponentScan 의 대상이 된다 */
@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	@Bean
	/*
	   LAZY Loading(지연로딩)시에 지연된 객체 대신에 Proxy객체을 loading 하면서 error 발생.
	   예를 들면, Order객체를 loading하는데 필드 중에 Member객체가 있어서 함께 loading을 하는데 이상한 애(Proxy - ByteBuddyInterceptor)가 있어서 가져올 수 없어 error 발생
	   Order를 조회할 때는 Order만 조회하고, LAZY Loading인 Member와 Delivery는 Loading하지 않는다. 기본적으로 1대다는 LAZY여서 이것도 Loading하지 않는다.
	   LAZY Loaing 인 경우에 Json에게 아무것도 하지 말라고 하는 것이 Hibernate5Module 이다.
	*/
	Hibernate5Module hibernate5Module() {
		Hibernate5Module hibernate5Module = new Hibernate5Module();
		//hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true); //이렇게 하면 엔티티가 다 노출되어, 엔티티가 바뀌면 API도 바뀌어야 해서 난리난다.
		return hibernate5Module;
	}
}
