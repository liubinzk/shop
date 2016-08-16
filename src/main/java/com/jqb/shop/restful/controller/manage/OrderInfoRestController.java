package com.jqb.shop.restful.controller.manage;


import com.jqb.shop.Filter;
import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.entity.*;
import com.jqb.shop.entity.groupbuying.Ticket;
import com.jqb.shop.restful.entity.RestfulConstants;
import com.jqb.shop.restful.entity.RestfulResult;
import com.jqb.shop.service.MemberService;
import com.jqb.shop.service.OrderService;
import com.jqb.shop.service.groupbuying.TicketService;
import com.jqb.shop.util.CommonUtils;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value="rest")
public class OrderInfoRestController {

    @Resource(name = "orderServiceImpl")
    private OrderService orderService;

    @Resource(name = "memberServiceImpl")
    private MemberService memberService;

    @Resource(name = "ticketServiceImpl")
    private TicketService ticketService;

    @RequestMapping(value = "/order_count")
    public
    @ResponseBody
    String getOrderList(Model model, HttpServletRequest request) {
//        request.getSession().setAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME,
//        new Principal(64L, "shopping"));
        RestfulResult restfulResult = new RestfulResult();
        String result = "";
        String callback = request.getParameter("callback");

        String userId = request.getParameter("userId");

        Integer pageNumber = null;
        String pageNumberStr = request.getParameter("pageNumber");
        if (pageNumberStr != null && !"".equals(pageNumberStr)) {
            pageNumber = Integer.parseInt(pageNumberStr);
        }

        Integer orderStatus = -1;
        String orderStatusStr = request.getParameter("orderStatus");
        if (orderStatusStr != null && !"".equals(orderStatusStr)) {
            orderStatus = Integer.parseInt(orderStatusStr);
        }

        Integer paymentStatus = -1;
        String paymentStatusStr = request.getParameter("paymentStatus");
        if (paymentStatusStr != null && !"".equals(paymentStatusStr)) {
            paymentStatus = Integer.parseInt(paymentStatusStr);
        }
        Integer shippingStatus = -1;
        String shippingStatusStr = request.getParameter("shippingStatus");
        if (shippingStatusStr != null && !"".equals(shippingStatusStr)) {
            shippingStatus = Integer.parseInt(shippingStatusStr);
        }
        Filter stautsFilter = new Filter("orderStatus", Filter.Operator.eq, getOrderStatus( orderStatus ) );
        Filter paymentFilter = new Filter("paymentStatus", Filter.Operator.eq, getPaymentStatus( paymentStatus ) );
        Filter shippingFilter = new Filter("shippingStatus", Filter.Operator.eq,getShippingStatus( shippingStatus ));
        List<Filter> filters = new ArrayList<Filter>();
        filters.add( stautsFilter );
        filters.add( paymentFilter );
        filters.add( shippingFilter );

        List<Order> orderList = null;
        try {
            Member member = null;
            if(userId != null && !"".equals(userId)){
                member = memberService.findByUid(Long.parseLong(userId));
            }
            if(member == null){
                orderList = orderService.findList(null,filters,null);
            } else {
                orderList = orderService.findList(member,null,filters,null);
            }
            Ticket ticket =  ticketService.findByCode("870629518244");
            System.out.println("ticket for use : " + ticket.getProductid());
//            String sql = "SELECT count(id),order_status FROM shopjqb.xx_order where member=62 group by order_status";
//            List<List<String>> restList = (List<List<String>> )orderService.execute(sql);
//            System.out.println("sql result :" + restList.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return CommonUtils.returnRestfulResult(callback, restfulResult);
        }
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        if(orderList != null){
            restfulResult.setReturnObj(orderList.size());
        }
        return CommonUtils.returnRestfulResult(callback, restfulResult);
    }
    private Order.OrderStatus getOrderStatus(int status){
        switch (status){
            case 0 : return Order.OrderStatus.unconfirmed;
            case 1 : return Order.OrderStatus.confirmed;
            case 2 : return Order.OrderStatus.completed;
            case 3 : return Order.OrderStatus.cancelled;
            case 4 : return Order.OrderStatus.deleted;
            default:return null;
        }
    }

    private Order.PaymentStatus getPaymentStatus(int status){
        switch (status){
            case 0 : return Order.PaymentStatus.unpaid;
            case 1 : return Order.PaymentStatus.partialPayment;
            case 2 : return Order.PaymentStatus.paid;
            case 3 : return Order.PaymentStatus.partialRefunds;
            case 4 : return Order.PaymentStatus.refunded;
            default:return null;
        }
    }
    private Order.ShippingStatus getShippingStatus(int status){
        switch (status){
            case 0 : return Order.ShippingStatus.unshipped;
            case 1 : return Order.ShippingStatus.partialShipment;
            case 2 : return Order.ShippingStatus.shipped;
            case 3 : return Order.ShippingStatus.partialReturns;
            case 4 : return Order.ShippingStatus.returned;
            default:return null;
        }
    }
}