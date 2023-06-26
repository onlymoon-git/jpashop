package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String createForm(Model model) {

        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long ItemId,
                        @RequestParam("count") int count) {

        Long orderId = orderService.order(memberId, ItemId, count);

        return "redirect:/orders";
        /* 주문된 결과페이지로 가려면 orderService의 결과값으로 orderId를 받아서 아래 페이지를 만들면 된다.
        return "redirect:/order/" + orderId;
        */
    }

    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {
        List<Order> orders = orderService.findOrders(orderSearch); //이런 단순 조회를 Service에 단순 위임이면 Controller에서 바로 Repository를 불러도 된다.(Service를 거치지 않고)
        model.addAttribute("orders", orders);

        //System.out.println("OrderController-orderList: Print test");
        log.info("OrderController-orderList: Print test");

        return "order/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {

        //System.out.println("OrderController-cancelOrder: orderId = " + orderId);
        log.info("OrderController-cancelOrder: orderId = " + orderId);
        orderService.cancelOrder(orderId);

        return "redirect:/orders";
    }
}
