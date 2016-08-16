package com.jqb.shop.restful.controller.pay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.jqb.shop.Principal;
import com.jqb.shop.entity.*;
import com.jqb.shop.plugin.alipay.config.AlipayConfig;
import com.jqb.shop.plugin.alipay.factory.AlipayAPIClientFactory;
import com.jqb.shop.plugin.alipay.utils.*;
import com.jqb.shop.plugin.wxpay.api.CommonUtils;
import com.jqb.shop.plugin.wxpay.api.WxPayData;
import com.jqb.shop.plugin.wxpay.lib.WxPayException;
import com.jqb.shop.restful.entity.RestfulConstants;
import com.jqb.shop.restful.entity.RestfulResult;
import com.jqb.shop.service.*;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by liubin on 2016/1/8.
 */
@Controller
@RequestMapping(value="rest")
public class ToAlipayTradePayController {

    private static Logger Log = Logger.getLogger(ToAlipayTradePayController.class);

    private static String payMethodName="支付宝支付";

    @Resource(name = "orderServiceImpl")
    private OrderService orderService;

    @Resource(name = "customerOrderServiceImpl")
    private CustomerOrderService customerOrderService;

    @Resource(name = "memberServiceImpl")
    private MemberService memberService;

    @Resource(name = "paymentMethodServiceImpl")
    private PaymentMethodService paymentMethodService;

    @Resource(name = "receiverServiceImpl")
    private ReceiverService receiverService;

    @RequestMapping(value = "/alipay_order")
    public @ResponseBody
    String tradePay( HttpServletRequest request,String out_trade_no) {

        String result = "";
        String callback = request.getParameter("callback");
        String memo = request.getParameter("memo");
        String receiverIdStr = request.getParameter("receiverId");
        long receiverId = 0;
        if (receiverIdStr != null && !"".equals(receiverIdStr)) {
            receiverId = Long.parseLong(receiverIdStr);
        }
        String JSESSIONID = request.getQueryString();
//                request.getSession().getId();
        Order order = null;
        CustomerOrder customerOrder = null;
        String subject = "";
        String body = "";
        double total_fee=0;
//        Member member = memberService.getCurrent();
        String orderInfo = null;
        String sign = null;
        try {
            if (out_trade_no != null) {
                order = orderService.findBySn(out_trade_no );
                if(order == null){
                    if (out_trade_no != null) {
                        customerOrder = customerOrderService.findBySn(out_trade_no );
                    }
                    if (customerOrder != null ) {
                        PaymentMethod paymentMethod = paymentMethodService.find(2L);
                        customerOrder.setPaymentMethod(paymentMethod);
                        customerOrder.setPaymentMethodName(this.payMethodName);
                        if(customerOrder.getRealOrders() != null){
                            for (Order realOrder : customerOrder.getRealOrders()) {
                                realOrder.setPaymentMethod(paymentMethod);
                                realOrder.setPaymentMethodName(this.payMethodName);
                            }
                        }
                        if(memo != null && !memo.equals(customerOrder.getMemo())){
                            customerOrder.setMemo(memo);
                        }
                        if(receiverId != 0){
                           Receiver receiver = receiverService.find( receiverId );
                            if(receiver != null){
                                if(order.getConsignee() == null){
                                    order.setConsignee(receiver.getConsignee());
                                    order.setAreaName(receiver.getAreaName());
                                    order.setAddress(receiver.getAddress());
                                    order.setZipCode(receiver.getZipCode());
                                    order.setPhone(receiver.getPhone());
                                    order.setArea(receiver.getArea());
                                } else {
                                    if(!order.getConsignee().equalsIgnoreCase(receiver.getConsignee())){
                                        order.setConsignee(receiver.getConsignee());
                                        order.setAreaName(receiver.getAreaName());
                                        order.setAddress(receiver.getAddress());
                                        order.setZipCode(receiver.getZipCode());
                                        order.setPhone(receiver.getPhone());
                                        order.setArea(receiver.getArea());
                                    }
                                }
                            }
                        }
                        customerOrderService.update( customerOrder );
                        if (!customerOrder.isExpired() && customerOrder.getPaymentStatus() == CustomerOrder.PaymentStatus.unpaid  ) {
                            subject = customerOrder.getName();
                            body = ""+customerOrder.getId();
                            total_fee = com.jqb.shop.util.CommonUtils.formatAliFee( customerOrder.getAmount() );

                        } else {
                            return returnResult(callback, "订单已支付");
                        }
                    } else {
                        return returnResult(callback, "订单失效");
                    }
                } else {
                    if (order != null ) {
                        PaymentMethod paymentMethod = paymentMethodService.find(2L);
                        order.setPaymentMethod(paymentMethod);
                        order.setPaymentMethodName(this.payMethodName);
                        if(memo != null && !memo.equals(order.getMemo())){
                            order.setMemo(memo);
                        }
                        orderService.update( order );
                        if (!order.isExpired() && order.getPaymentStatus() == Order.PaymentStatus.unpaid  ) {
                            subject = order.getOrderItems().get(0).getFullName();
                            body = order.getOrderItems().get(0).getFullName();
                            total_fee = com.jqb.shop.util.CommonUtils.formatAliFee(order.getAmount());
                        } else {
                            return returnResult(callback, "订单已支付");
                        }
                    } else {
                        return returnResult(callback, "订单失效");
                    }
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time_expire= sdf.format(System.currentTimeMillis()+24*60*60*1000);


            String method = "mobile.securitypay.pay";
            String sign_type = "RSA";
            String version = "1.0";
            String notify_url = "http://shop.tipuyou.cn/shop/rest/alipay_result?orderId="+out_trade_no;
            String return_url = "http://shop.tipuyou.cn/shopmobile/pay/payresult";
            String charset = "utf-8";
            int payment_type = 1;
//            total_fee=0.01;

            Map<String, String> orderMap = new HashMap<String, String>();
            orderMap.put("service", method);
            orderMap.put("partner", AlipayConfig.PARTNER);
            orderMap.put("_input_charset", AlipayConfig.INPUT_CHARSET);
            orderMap.put("out_trade_no", out_trade_no);
            orderMap.put("subject", subject);
            orderMap.put("payment_type", "1");
            orderMap.put("notify_url", notify_url);
            orderMap.put("seller_id", AlipayConfig.PARTNER);
            orderMap.put("total_fee", "" + total_fee);
            orderMap.put("body", body);
            orderMap.put("it_b_pay", "30m");
            orderMap.put("return_url", return_url);

            StringBuilder sb = new StringBuilder();
            // 对订单做RSA 签名
            orderInfo = AlipaySubmit.getOrderInfo(orderMap);
            sign = AlipaySubmit.makeMobileSign(orderMap);
        } catch (NumberFormatException e) {
            RestfulResult restfulResult = new RestfulResult();
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback, restfulResult);
        }
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + AlipaySubmit.getSignType();
        try {
            payInfo = URLEncoder.encode(payInfo, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        result = "{\"biz_content\":\"" + payInfo + "\"}";
        if (callback == null) {
            return result;
        } else {
            return callback + "('" + result + "')";
        }
    }

    @RequestMapping(value = "/query_alipay_order")
    public @ResponseBody
    String tradeQuery( HttpServletRequest request,String out_trade_no) {
        String result = "";
        String callback = request.getParameter("callback");
        AlipayTradeQueryResponse response = null;
        try {
            response =  orderService.queryAliPayOrder(out_trade_no);
        } catch (Exception e) {
            Log.error(e.getMessage());
        }
        return callback + "('" + response.getBody() + "')";
    }

    @RequestMapping(value = "/alipay_customer_result")
    public @ResponseBody
    String payCustomerOrderSuccess( HttpServletRequest request,  HttpServletResponse response) {
        Log.info("unifiedorder is processing...");
        RestfulResult restfulResult = new RestfulResult();
        String result = "";
        String callback = request.getParameter("callback");
        String customerOrderSn = request.getParameter("customerOrderSn");
        String errCode = request.getParameter("errCode");
        CustomerOrder order = null;
        try {
            if (customerOrderSn != null) {
                order = customerOrderService.findBySn(customerOrderSn);
            }
            if (errCode.equals("9000")) {
                if(order.getPaymentStatus() != CustomerOrder.PaymentStatus.paid){

                    if(order.getRealOrders() != null){
                        for (Order realOrder : order.getRealOrders()) {
                            realOrder.setPaymentStatus(Order.PaymentStatus.paid);
                            realOrder.setPaymentMethodName(this.payMethodName);
                            realOrder.setAmountPaid(realOrder.getAmount() );
                            orderService.update(realOrder);
                        }
                    }
                    order.setPaymentStatus(CustomerOrder.PaymentStatus.paid);
                    order.setPaymentMethodName(this.payMethodName);
                    order.setAmountPaid(order.getAmount());
                    customerOrderService.update(order);
                }
                restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
            }
            if(order != null){
                if (order.getMember() != null) {
                    Member newMember = new Member();
                    order.getMember().initRestEntiy();
                    newMember.setId(order.getMember().getId());
                    newMember.setIsLocked(order.getMember().getIsLocked());
                    newMember.setUsername(order.getMember().getUsername());
                    newMember.initRestEntiy();
                    order.setMember(newMember);
                }
                if (order.getArea() != null) {
                    order.setAreaName(order.getArea().getFullName());
                    order.setArea(null);
                }
                order.initRestEntity();
                restfulResult.setReturnObj(order);
            }
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback, restfulResult);
        }
        JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
        jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
            public boolean apply(Object obj, String name, Object value) {
                if (obj instanceof OrderItem && name.equals("order")) {
                    return true;
                } else if (obj instanceof Product && value == null) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("handler")) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("hibernateLazyInitializer")) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("orderItem")) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("cartItems")) {
                    return true;
                } else if (obj instanceof Product && name.equals("specifications")) {
                    return true;
                } else if (obj instanceof Product && name.equals("promotions")) {
                    return true;
                } else if (obj instanceof Product && name.equals("tags")) {
                    return true;
                } else if (obj instanceof Product && name.equals("specificationValues")) {
                    return true;
                } else if (obj instanceof Product && name.equals("reviews")) {
                    return true;
                } else if (obj instanceof Product && name.equals("productNotifies")) {
                    return true;
                } else if (obj instanceof Product && name.equals("consultations")) {
                    return true;
                } else if (obj instanceof Product && name.equals("brand")) {
                    return true;
                } else if (obj instanceof Product && name.equals("favoriteMembers")) {
                    return true;
                } else if (obj instanceof Product && name.equals("giftItems")) {
                    return true;
                } else if (obj instanceof Product && name.equals("goods")) {
                    return true;
                } else if (obj instanceof Product && name.equals("memberPrice")) {
                    return true;
                } else if (obj instanceof Product && name.equals("productCategory")) {
                    return true;
                } else if (obj instanceof Product && name.equals("productImages")) {
                    return true;
                } else if (obj instanceof Product && name.equals("area")) {
                    return true;
                } else if (obj instanceof Product && name.equals("parameterValue")) {
                    return true;
                } else if (obj instanceof Member || name.equals("orders")) {//Member
                    return true;
                } else if (obj instanceof Returns && name.equals("order")) {//Member
                    return true;
                } else if (obj instanceof Returns && name.equals("returnsItems")) {//Member
                    return true;
                } else if (obj instanceof Shipping && name.equals("shippingItems")) {//Member
                    return true;
                } else if (obj instanceof PaymentMethod && name.equals("orders")) {//PaymentMethod
                    return true;
                } else if (obj instanceof PaymentMethod && name.equals("shippingMethods")) {//PaymentMethod
                    return true;
                } else if (obj instanceof PaymentMethod && name.equals("content")) {//PaymentMethod
                    return true;
                } else if (obj instanceof Shipping && name.equals("order")) {//Member
                    return true;
                } else if (obj instanceof Order && name.equals("customerOrder")) {//Member
                    return true;
                } else if (obj instanceof CustomerOrder && name.equals("realOrders")) {//Member
                    return true;
                } else {
                    return false;
                }
            }
        });
        jsonConfig.setIgnoreDefaultExcludes(false);  //设置默认忽略

        jsonConfig.setExcludes(new String[]{"handler","hibernateLazyInitializer"});

        return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback, restfulResult, jsonConfig);

    }
    @RequestMapping(value = "/alipay_result")
    public @ResponseBody
    String payOrderSuccess( HttpServletRequest request,  HttpServletResponse response) {
        Log.info("unifiedorder is processing...");
        RestfulResult restfulResult = new RestfulResult();
        String result = "";
        String callback = request.getParameter("callback");
        String orderSn = request.getParameter("orderSn");
        String errCode = request.getParameter("errCode");
        Order order = null;
        try {
            if (orderSn != null) {
                order = orderService.findBySn( orderSn );
            }
            if (errCode.equals("9000")) {
                if(order.getPaymentStatus() != Order.PaymentStatus.paid){
                    order.setPaymentStatus(Order.PaymentStatus.paid);
                    order.setPaymentMethodName(this.payMethodName);
                    order.setAmountPaid(order.getAmount());
                    orderService.update(order);
                }
                restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
            }
            if(order != null){
                order.initRestEntity();
                restfulResult.setReturnObj(order);
            }
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback, restfulResult);
        }
        JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
        jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
            public boolean apply(Object obj, String name, Object value) {
                if(obj instanceof OrderItem && name.equals("order")){
                    return true;
                } else  if(obj instanceof OrderItem && name.equals("product") ){//Prodect init
                    return true;
                } else  if(obj instanceof Order && name.equals("area") ){//Prodect init
                    return true;
                } else if(obj instanceof Member || name.equals("orders")){//Member
                    return true;
                } else if(obj instanceof Returns && name.equals("order")){//Member
                    return true;
                } else if(obj instanceof Returns && name.equals("returnsItems")){//Member
                    return true;
                } else if(obj instanceof Shipping && name.equals("shippingItems")){//Member
                    return true;
                } else if(obj instanceof Shipping && name.equals("order")){//Member
                    return true;
                } else if(obj instanceof ShippingMethod && name.equals("defaultDeliveryCorp")){//ShippingMethod
                    return true;
                }  else if(obj instanceof ShippingMethod && name.equals("paymentMethods")){//ShippingMethod
                    return true;
                }  else if(obj instanceof ShippingMethod && name.equals("orders")){//ShippingMethod
                    return true;
                }  else if(obj instanceof Order && name.equals("customerOrder")){//Member
                    return true;
                }  else if(obj instanceof Order && name.equals("exchangeForOrder")){//Member
                    return true;
                }  else {
                    return false;
                }
            }
        });
        jsonConfig.setIgnoreDefaultExcludes(false);  //设置默认忽略

        jsonConfig.setExcludes(new String[]{"handler","hibernateLazyInitializer"});

        return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback, restfulResult, jsonConfig);

    }

    @RequestMapping(value = "/alipay_notify")
    public @ResponseBody
    String payOrderNotify( HttpServletRequest request,  HttpServletResponse response) {
        Log.info("alipay dorder notify begin ...");
        RestfulResult restfulResult = new RestfulResult();
        String result = "";
        String callback = request.getParameter("callback");
        String orderIdstr = request.getParameter("orderId");
        Order order = null;
        if (orderIdstr != null) {
            order = orderService.find(Long.parseLong(orderIdstr));
            if(order != null && order.getPaymentStatus() != Order.PaymentStatus.paid){
                order.setPaymentStatus(Order.PaymentStatus.paid);
                order.setPaymentMethodName(this.payMethodName);
                order.setAmountPaid( order.getAmount() );
                orderService.update(order);
            }
            Log.info("alipay dorder notify :update success.");
        }
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback, restfulResult);

    }

    @RequestMapping(value = "/alipay_result_check")
    public @ResponseBody
    String payOrderCallback( HttpServletRequest request,  HttpServletResponse response) {
        Log.info("unifiedorder is processing...");
        String result = "";
        String callback = request.getParameter("callback");

        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
        //商户订单号

        String out_trade_no = null;
        try {
            out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //支付宝交易号

        String trade_no = null;
        try {
            trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //交易状态
        String trade_status = null;
        try {
            trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        System.out.println("out_trade_no:"+out_trade_no);
        System.out.println("trade_no:"+trade_no);
        System.out.println("trade_status:"+trade_status);

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

        if(AlipayNotify.verify(params)){//验证成功
            //////////////////////////////////////////////////////////////////////////////////////////
            //请在这里加上商户的业务逻辑程序代码

            System.out.println("verify:success");







            //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——

            //判断是否在商户网站中已经做过了这次通知返回的处理
            //如果没有做过处理，那么执行商户的业务程序
            //如果有做过处理，那么不执行商户的业务程序

            System.out.println("success");	//请不要修改或删除

            //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——

            //////////////////////////////////////////////////////////////////////////////////////////
        }else{//验证失败
            System.out.println("fail");
        }

        if (callback == null) {
            return result;
        } else {
            return callback + "('" + result + "')";
        }
    }
    private String returnResult(String callback, String result) {
        if (callback == null) {
            return result;
        } else {
            return callback + "('" + result + "')";
        }
    }

    public static void main(String[] args) {
        ToAlipayTradePayController toAlipayTradePayController = new ToAlipayTradePayController();
        String orderId = "2016050415151";
//        String orderSn = "2016050415151";
////        toAlipayTradePayController.tradeAlipay(orderId);
//        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.ALIPAY_GATEWAY,AlipayConfig.TIPS_APPID,AlipayConfig.PRIVATE_KEY,"json","GBK",AlipayConfig.ALIPAY_PUBLIC_KEY);
//        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
//
////        String url=toAlipayTradePayController.tradeQuery(orderSn);
////        System.out.println(url);
////
////        request.setBizContent(url);
//        request.setBizContent("{\"out_trade_no\":\"" + orderSn + "\"," +
//                "\"trade_no\":\"" + orderSn +"\"}");
//        AlipayTradeQueryResponse response = null;
//        try {
//            response = alipayClient.execute(request);
//        } catch (AlipayApiException e) {
//            e.printStackTrace();
//        }
//        System.out.println(response);
        String out_trade_no = "2016050515352";
        try {
            toAlipayTradePayController.queryAliPayOrder(out_trade_no);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AlipayTradeQueryResponse queryAliPayOrder(String out_trade_no) throws  Exception{
        AlipayClient alipayClient =  new DefaultAlipayClient(AlipayConfig.ALIPAY_GATEWAY,AlipayConfig.TIPS_APPID,AlipayConfig.PRIVATE_KEY,"json","GBK", AlipayConfig.TIPU_ALIPAY_PUBLIC_KEY);
        AlipayTradeQueryRequest  request = new AlipayTradeQueryRequest();
        request.setBizContent("{" +
                "    \"out_trade_no\":\"" + out_trade_no + "\"," +
                "    \"trade_no\":\"\"" +
                "  }");
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        System.out.println("response order:" + response.getBody());
        return response;
    }
}
