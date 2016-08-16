package com.jqb.shop.restful.controller;

import com.jqb.shop.*;
import com.jqb.shop.Message;
import com.jqb.shop.entity.*;
import com.jqb.shop.entity.Order;
import com.jqb.shop.restful.entity.RestfulConstants;
import com.jqb.shop.restful.entity.RestfulResult;
import com.jqb.shop.service.*;
import com.jqb.shop.util.CommonUtils;
import com.jqb.shop.util.SpringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by liubin on 2015/12/23.
 */
@Controller
@RequestMapping(value="rest")
public class OrderRestController {
    /**
     * "验证结果"参数名称
     */
    private static final String CONSTRAINT_VIOLATIONS_ATTRIBUTE_NAME = "constraintViolations";

    static final int PAGE_SIZE = 10;

    @Resource(name = "cartServiceImpl")
    private CartService cartService;

    @Resource(name = "orderServiceImpl")
    private OrderService orderService;

    @Resource(name = "receiverServiceImpl")
    private ReceiverService receiverService;

    @Resource(name = "paymentMethodServiceImpl")
    private PaymentMethodService paymentMethodService;

    @Resource(name = "shippingMethodServiceImpl")
    private ShippingMethodService shippingMethodService;

    @Resource(name = "couponCodeServiceImpl")
    private CouponCodeService couponCodeService;

    @Resource(name = "memberServiceImpl")
    private MemberService memberService;

    @Resource(name = "productServiceImpl")
    private ProductService productService;
    @Resource(name = "cartItemServiceImpl")
    private CartItemService cartItemService;

    @Resource(name="snServiceImpl")
    private SnService snService;
    @Resource(name="customerOrderServiceImpl")
    private CustomerOrderService customerOrderService;


    @RequestMapping(value = "/cart_info")
    public @ResponseBody
    String getShoppingCart(Model model, HttpServletRequest request) {
        String result = "";
        String callback = request.getParameter("callback");

        String cartIdStr = request.getParameter("cartId");
        long cartId = 0;
        if (cartIdStr != null && !"".equals(cartIdStr)) {
            cartId = Long.parseLong(cartIdStr);
        }
        JSONObject jb = null;
        try {
            Cart cart = cartService.find(cartId);
            if (cart == null || cart.isEmpty()) {
                result = "请您需要的选择商品。";
                return returnResult(callback, result);
            }
            if (!isValid(cart)) {
                result = "Sorry! Please selcet products.";
                return returnResult(callback, result);
            }
            Order order = orderService.build(cart, null, null, null, null, false, null, false, null);


            jb = JSONObject.fromObject("{\"result\":\"sucess\"}");
        } catch (Exception e) {
            RestfulResult restfulResult = new RestfulResult();
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        result = jb.toString();
        if (callback == null) {
            return result;
        } else {
            return callback + "('" + result + "')";
        }
    }
    @RequestMapping(value = "/save_customer_order")
    public
    @ResponseBody
    String saveCustomerOrder(Model model, HttpServletRequest request) {

        String result = "";
        String callback = request.getParameter("callback");

        String receiverIdStr = request.getParameter("receiverId");
        long receiverId = 0;
        if (receiverIdStr != null && !"".equals(receiverIdStr)) {
            receiverId = Long.parseLong(receiverIdStr);
        }


        String shippingMethodIdStr = request.getParameter("shippingMethodId");
        long shippingMethodId = 0;
        if (shippingMethodIdStr != null && !"".equals(shippingMethodIdStr)) {
            shippingMethodId = Long.parseLong(shippingMethodIdStr);
        }
        String paymentMethodIdStr = request.getParameter("paymentMethodId");
        long paymentMethodId = 0;
        if (paymentMethodIdStr != null && !"".equals(paymentMethodIdStr)) {
            paymentMethodId = Long.parseLong(paymentMethodIdStr);
        }

        String code = request.getParameter("code");

        //发票
        boolean isInvoice = true;
        String isInvoiceStr = request.getParameter("isInvoice");
        if (isInvoiceStr != null && !"".equals(isInvoiceStr)) {
            isInvoice = Boolean.parseBoolean(isInvoiceStr);
        }
        String invoiceTitle = request.getParameter("invoiceTitle");

        String memo = request.getParameter("memo");

        String cartIdStr = request.getParameter("cartId");
        long cartId = 0;
        if (cartIdStr != null && !"".equals(cartIdStr)) {
            cartId = Long.parseLong(cartIdStr);
        }
        Order order = null;
        try {
            Cart cart = cartService.find(cartId);
            if (cart == null || cart.isEmpty()) {
                RestfulResult restfulResult = new RestfulResult();
                restfulResult.setResult("Sorry!Please select product.");
                return  returnRestfulResult(callback, restfulResult);
            }

            if (!isValid(cart)) {
                result = "Sorry! Please selcet products.";
                return returnResult(callback, result);
            }

            //product restriction
            Member member = memberService.getCurrent();
            OrderResponder orderResponder = orderService.checkProduct(member,cart);
            if(!orderResponder.isValide()){
                return CommonUtils.returnRestfulResult(callback,orderResponder.getRestfulResult());
            }
            if (cart.getIsLowStock()) {
                result = SpringUtils.getMessage("shop.order.cartLowStock");
                return returnResult(callback, result);
            }
            //地址 去掉地址检查
            Receiver receiver = receiverService.find(receiverId);
//            if (receiver == null) {
//                result = SpringUtils.getMessage("shop.order.receiverNotExsit");
//                return returnResult(callback, result);
//            }
            //支付
            PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
            if (paymentMethod == null) {
                result = SpringUtils.getMessage("shop.order.paymentMethodNotExsit");
                return returnResult(callback, result);
            }
            ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
            if (shippingMethod == null) {
                result = SpringUtils.getMessage("shop.order.shippingMethodNotExsit");
                return returnResult(callback, result);
            }
            //包含运费
//        if (!paymentMethod.getShippingMethods().contains(shippingMethod)) {
//            result =  SpringUtils.getMessage("shop.order.deliveryUnsupported");
//            return returnResult(callback, result);
//        }
            CouponCode couponCode = couponCodeService.findByCode(code);
            //发票 isInvoice


            //优惠券
//        boolean useBalance = false;
//        String useBalanceStr = request.getParameter("useBalance");
//        if ( useBalanceStr!=null && !"".equals(useBalanceStr)) {
//            useBalance = Boolean.parseBoolean(useBalanceStr);
//        }
            order = orderService.createMOrder(cart, receiver, paymentMethod, shippingMethod, couponCode, isInvoice, invoiceTitle, false, memo, null);
        } catch (Exception e) {
            RestfulResult restfulResult = new RestfulResult();
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        RestfulResult restfulResult = new RestfulResult();
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult("" + order.getSn());
        return returnRestfulResult(callback,restfulResult);
    }

    @RequestMapping(value = "/create_customer_order")
    public
    @ResponseBody
    String createCustomerOrder(Model model, HttpServletRequest request) {

        String result = "";
        String callback = request.getParameter("callback");

        String receiverIdStr = request.getParameter("receiverId");
        long receiverId = 0;
        if (receiverIdStr != null && !"".equals(receiverIdStr)) {
            receiverId = Long.parseLong(receiverIdStr);
        }


        String shippingMethodIdStr = request.getParameter("shippingMethodId");
        long shippingMethodId = 0;
        if (shippingMethodIdStr != null && !"".equals(shippingMethodIdStr)) {
            shippingMethodId = Long.parseLong(shippingMethodIdStr);
        }
        String paymentMethodIdStr = request.getParameter("paymentMethodId");
        long paymentMethodId = 0;
        if (paymentMethodIdStr != null && !"".equals(paymentMethodIdStr)) {
            paymentMethodId = Long.parseLong(paymentMethodIdStr);
        }

        String code = request.getParameter("code");

        //地址
        Receiver receiver = receiverService.find(receiverId);
        if (receiver == null) {
            result = SpringUtils.getMessage("shop.order.receiverNotExsit");
            return returnResult(callback, result);
        }

        //发票
        boolean isInvoice = true;
        String isInvoiceStr = request.getParameter("isInvoice");
        if (isInvoiceStr != null && !"".equals(isInvoiceStr)) {
            isInvoice = Boolean.parseBoolean(isInvoiceStr);
        }
        String invoiceTitle = request.getParameter("invoiceTitle");

        String memo = request.getParameter("memo");

        String cartIdStr = request.getParameter("cartId");
        long cartId = 0;
        if (cartIdStr != null && !"".equals(cartIdStr)) {
            cartId = Long.parseLong(cartIdStr);
        }
        CustomerOrder order = null;
        try {
            Cart cart = cartService.find(cartId);
            if (cart == null || cart.isEmpty()) {
                RestfulResult restfulResult = new RestfulResult();
                restfulResult.setResult("Sorry!Please select product.");
               return  returnRestfulResult(callback, restfulResult);
            }

            if (!isValid(cart)) {
                result = "Sorry! Please selcet products.";
                return returnResult(callback, result);
            }

            //check product restriction
            Member member = memberService.getCurrent();
            OrderResponder orderResponder = orderService.checkProduct(member, cart);
            if(!orderResponder.isValide()){
                return CommonUtils.returnRestfulResult(callback, orderResponder.getRestfulResult());
            }
            if (cart.getIsLowStock()) {
                result = SpringUtils.getMessage("shop.order.cartLowStock");
                return returnResult(callback, result);
            }

            //支付
            PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
            if (paymentMethod == null) {
                result = SpringUtils.getMessage("shop.order.paymentMethodNotExsit");
                return returnResult(callback, result);
            }
            ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
            if (shippingMethod == null) {
                result = SpringUtils.getMessage("shop.order.shippingMethodNotExsit");
                return returnResult(callback, result);
            }
            CouponCode couponCode = couponCodeService.findByCode(code);
            List<Cart> shopCartList =  orderService.divCartByShop(cart);
            if(shopCartList != null){
                List<Order> realOrderList = new ArrayList<Order>();
                order = new CustomerOrder();
                order.setSn(snService.generate(Sn.Type.order));
                customerOrderService.save(order);
                for (Cart shopCart : shopCartList) {
                    Order  realOrder = orderService.createCustomerOrder(order, shopCart, receiver, paymentMethod, shippingMethod, couponCode, isInvoice, invoiceTitle, false, memo, null);
                    realOrderList.add(realOrder);
                }
                cartService.delete(cart);
                this.generateCustomerOrder( order, realOrderList.get(0));
                customerOrderService.update(order);
            }
        } catch (Exception e) {
            RestfulResult restfulResult = new RestfulResult();
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        RestfulResult restfulResult = new RestfulResult();
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult("" + order.getSn());
        return returnRestfulResult(callback,restfulResult);
    }
    @RequestMapping(value = "/order/save_immediate_order")
    public
    @ResponseBody
    String saveImmediateOrder(Long id, Integer quantity, HttpServletRequest request) {
        RestfulResult restfulResult = new RestfulResult();
        String callback = request.getParameter("callback");
        String receiverIdStr = request.getParameter("receiverId");
        long receiverId = 0;
        if (receiverIdStr != null && !"".equals(receiverIdStr)) {
            receiverId = Long.parseLong(receiverIdStr);
        }
        String shippingMethodIdStr = request.getParameter("shippingMethodId");
        long shippingMethodId = 0;
        if (shippingMethodIdStr != null && !"".equals(shippingMethodIdStr)) {
            shippingMethodId = Long.parseLong(shippingMethodIdStr);
        }
        String paymentMethodIdStr = request.getParameter("paymentMethodId");
        long paymentMethodId = 0;
        if (paymentMethodIdStr != null && !"".equals(paymentMethodIdStr)) {
            paymentMethodId = Long.parseLong(paymentMethodIdStr);
        }

        String code = request.getParameter("code");

        //发票
        boolean isInvoice = true;
        String isInvoiceStr = request.getParameter("isInvoice");
        if (isInvoiceStr != null && !"".equals(isInvoiceStr)) {
            isInvoice = Boolean.parseBoolean(isInvoiceStr);
        }
        String invoiceTitle = request.getParameter("invoiceTitle");

        String memo = request.getParameter("memo");
        String result = "";
        if (quantity == null || quantity < 1) {
            result =   com.jqb.shop.Message.error("shop.message.error").getContent();
            restfulResult.setResult(result);
            return CommonUtils.returnRestfulResult(callback, restfulResult);
        }
        Cart cart = null;
        Member member = null;
        Order order = null;
        try {
            Product product = productService.find(id);
            if (product == null) {
                result = com.jqb.shop.Message.warn("shop.cart.productNotExsit").getContent();
                restfulResult.setResult(result);
                return CommonUtils.returnRestfulResult(callback, restfulResult);
            }
            if (!product.getIsMarketable()) {
                result = com.jqb.shop.Message.warn("shop.cart.productNotMarketable").getContent();
                restfulResult.setResult(result);
                return CommonUtils.returnRestfulResult(callback, restfulResult);
            }
            if (product.getIsGift()) {
                result = com.jqb.shop.Message.warn("shop.cart.notForSale").getContent();
                restfulResult.setResult(result);
                return CommonUtils.returnRestfulResult(callback, restfulResult);
            }

            member = memberService.getCurrent();

            if (cart == null) {
                cart = new Cart();
                cart.setKey(UUID.randomUUID().toString() + DigestUtils.md5Hex(RandomStringUtils.randomAlphabetic(30)));
                cart.setMember(member);
                //            cart = cartService.getCurrent();
                //result = com.jqb.shop.Message.success("shop.cart.addSuccess", cart.getQuantity(), currency(cart.getEffectivePrice(), true, false)).getContent();
                if(cart != null) {
                    result = ""+cart.getId();
                }
            }

            if (Cart.MAX_PRODUCT_COUNT != null && cart.getCartItems().size() >= Cart.MAX_PRODUCT_COUNT) {
                result =  com.jqb.shop.Message.warn("shop.cart.addCountNotAllowed", Cart.MAX_PRODUCT_COUNT).getContent();
                restfulResult.setResult(result);
                return CommonUtils.returnRestfulResult(callback, restfulResult);

            }


            if (CartItem.MAX_QUANTITY != null && quantity > CartItem.MAX_QUANTITY) {
                result = com.jqb.shop.Message.warn("shop.cart.maxCartItemQuantity", CartItem.MAX_QUANTITY).getContent();
                restfulResult.setResult(result);
                return CommonUtils.returnRestfulResult(callback, restfulResult);
            }
            if (product.getStock() != null && quantity > product.getAvailableStock()) {
                result = com.jqb.shop.Message.warn("shop.cart.productLowStock").getContent();
                restfulResult.setResult(result);
                return CommonUtils.returnRestfulResult(callback, restfulResult);
            }
            CartItem cartItem = new CartItem();
            cartItem.setQuantity(quantity);
            cartItem.setProduct(product);
            cartItem.setCart(cart);
            cart.getCartItems().add(cartItem);
            if (cart == null || cart.isEmpty()) {
                restfulResult.setResult("Sorry!Please select product.");
                return  returnRestfulResult(callback, restfulResult);
            }

            if (!isValid(cart)) {
                result = "Sorry! Please selcet products.";
                return returnResult(callback, result);
            }
            OrderResponder orderResponder = orderService.checkProduct(member,cart);
            if(!orderResponder.isValide()){
                return CommonUtils.returnRestfulResult(callback, orderResponder.getRestfulResult());
            }

            if (cart.getIsLowStock()) {
                result = SpringUtils.getMessage("shop.order.cartLowStock");
                return returnResult(callback, result);
            }
            //地址
            Receiver receiver = receiverService.find(receiverId);
//            if (receiver == null) {
//                result = SpringUtils.getMessage("shop.order.receiverNotExsit");
//                return returnResult(callback, result);
//            }
            //支付
            PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
            if (paymentMethod == null) {
                result = SpringUtils.getMessage("shop.order.paymentMethodNotExsit");
                return returnResult(callback, result);
            }
            ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
            if (shippingMethod == null) {
                result = SpringUtils.getMessage("shop.order.shippingMethodNotExsit");
                return returnResult(callback, result);
            }
            CouponCode couponCode = couponCodeService.findByCode(code);

            order = orderService.createMOrder(cart, receiver, paymentMethod, shippingMethod, couponCode, isInvoice, invoiceTitle, false, memo, null);


        } catch (Exception e) {

            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            restfulResult.setResult(result);
            return CommonUtils.returnRestfulResult(callback, restfulResult);
        }
        if(order != null){
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
            restfulResult.setResult("" + order.getSn() );
        } else {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_ORDER_FAIL);
            restfulResult.setResult("生成订单错误。");
        }

        return returnRestfulResult(callback, restfulResult);
    }
    @RequestMapping(value = "/order/create_immediate_order")
    public
    @ResponseBody
    String addImmediateCart(Long id, Integer quantity, HttpServletRequest request) {
        RestfulResult restfulResult = new RestfulResult();
        String callback = request.getParameter("callback");
        String receiverIdStr = request.getParameter("receiverId");
        long receiverId = 0;
        if (receiverIdStr != null && !"".equals(receiverIdStr)) {
            receiverId = Long.parseLong(receiverIdStr);
        }
        String shippingMethodIdStr = request.getParameter("shippingMethodId");
        long shippingMethodId = 0;
        if (shippingMethodIdStr != null && !"".equals(shippingMethodIdStr)) {
            shippingMethodId = Long.parseLong(shippingMethodIdStr);
        }
        String paymentMethodIdStr = request.getParameter("paymentMethodId");
        long paymentMethodId = 0;
        if (paymentMethodIdStr != null && !"".equals(paymentMethodIdStr)) {
            paymentMethodId = Long.parseLong(paymentMethodIdStr);
        }

        String code = request.getParameter("code");

        //发票
        boolean isInvoice = true;
        String isInvoiceStr = request.getParameter("isInvoice");
        if (isInvoiceStr != null && !"".equals(isInvoiceStr)) {
            isInvoice = Boolean.parseBoolean(isInvoiceStr);
        }
        String invoiceTitle = request.getParameter("invoiceTitle");

        String memo = request.getParameter("memo");
        String result = "";
        if (quantity == null || quantity < 1) {
            result =   com.jqb.shop.Message.error("shop.message.error").getContent();
            restfulResult.setResult(result);
            return CommonUtils.returnRestfulResult(callback, restfulResult);
        }
        Cart cart = null;
        Member member = null;
        Order order = null;
        try {
            Product product = productService.find(id);
            if (product == null) {
                result = com.jqb.shop.Message.warn("shop.cart.productNotExsit").getContent();
                restfulResult.setResult(result);
                return CommonUtils.returnRestfulResult(callback, restfulResult);
            }
            if (!product.getIsMarketable()) {
                result = com.jqb.shop.Message.warn("shop.cart.productNotMarketable").getContent();
                restfulResult.setResult(result);
                return CommonUtils.returnRestfulResult(callback, restfulResult);
            }
            if (product.getIsGift()) {
                result = com.jqb.shop.Message.warn("shop.cart.notForSale").getContent();
                restfulResult.setResult(result);
                return CommonUtils.returnRestfulResult(callback, restfulResult);
            }

            member = memberService.getCurrent();
            //地址
            Receiver receiver = receiverService.find(receiverId);
            if (receiver == null) {
                result = SpringUtils.getMessage("shop.order.receiverNotExsit");
                return returnResult(callback, result);
            }

            if (cart == null) {
                cart = new Cart();
                cart.setKey(UUID.randomUUID().toString() + DigestUtils.md5Hex(RandomStringUtils.randomAlphabetic(30)));
                cart.setMember(member);
                //            cart = cartService.getCurrent();
                //result = com.jqb.shop.Message.success("shop.cart.addSuccess", cart.getQuantity(), currency(cart.getEffectivePrice(), true, false)).getContent();
                if(cart != null) {
                    result = ""+cart.getId();
                }
            }

            if (Cart.MAX_PRODUCT_COUNT != null && cart.getCartItems().size() >= Cart.MAX_PRODUCT_COUNT) {
                result =  com.jqb.shop.Message.warn("shop.cart.addCountNotAllowed", Cart.MAX_PRODUCT_COUNT).getContent();
                restfulResult.setResult(result);
                return CommonUtils.returnRestfulResult(callback, restfulResult);

            }


                if (CartItem.MAX_QUANTITY != null && quantity > CartItem.MAX_QUANTITY) {
                    result = com.jqb.shop.Message.warn("shop.cart.maxCartItemQuantity", CartItem.MAX_QUANTITY).getContent();
                    restfulResult.setResult(result);
                    return CommonUtils.returnRestfulResult(callback, restfulResult);
                }
                if (product.getStock() != null && quantity > product.getAvailableStock()) {
                    result = com.jqb.shop.Message.warn("shop.cart.productLowStock").getContent();
                    restfulResult.setResult(result);
                    return CommonUtils.returnRestfulResult(callback, restfulResult);
                }
                CartItem cartItem = new CartItem();
                cartItem.setQuantity(quantity);
                cartItem.setProduct(product);
                cartItem.setCart(cart);
                cart.getCartItems().add(cartItem);
            if (cart == null || cart.isEmpty()) {
                restfulResult.setResult("Sorry!Please select product.");
                return  returnRestfulResult(callback, restfulResult);
            }

            if (!isValid(cart)) {
                result = "Sorry! Please selcet products.";
                return returnResult(callback, result);
            }
            //check product restriction
            OrderResponder orderResponder = orderService.checkProduct(member,cart);
            if(!orderResponder.isValide()){
                return CommonUtils.returnRestfulResult(callback, orderResponder.getRestfulResult());
            }
            if (cart.getIsLowStock()) {
                result = SpringUtils.getMessage("shop.order.cartLowStock");
                return returnResult(callback, result);
            }

            //支付
            PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
            if (paymentMethod == null) {
                result = SpringUtils.getMessage("shop.order.paymentMethodNotExsit");
                return returnResult(callback, result);
            }
            ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
            if (shippingMethod == null) {
                result = SpringUtils.getMessage("shop.order.shippingMethodNotExsit");
                return returnResult(callback, result);
            }
            CouponCode couponCode = couponCodeService.findByCode(code);

            order = orderService.createMOrder(cart, receiver, paymentMethod, shippingMethod, couponCode, isInvoice, invoiceTitle, false, memo, null);


        } catch (Exception e) {

            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            restfulResult.setResult(result);
            return CommonUtils.returnRestfulResult(callback, restfulResult);
        }
        if(order != null){
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
            restfulResult.setResult("" + order.getSn() );
        } else {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_ORDER_FAIL);
            restfulResult.setResult("生成订单错误。");
        }

        return returnRestfulResult(callback, restfulResult);
    }

    @RequestMapping(value = "/order_list")
    public
    @ResponseBody
    String getOrderList(Model model, HttpServletRequest request) {
//        request.getSession().setAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME,
//        new Principal(64L, "shopping"));
        RestfulResult restfulResult = new RestfulResult();
        String result = "";
        String callback = request.getParameter("callback");

        Integer pageNumber = null;
        String pageNumberStr = request.getParameter("pageNumber");
        if (pageNumberStr != null && !"".equals(pageNumberStr)) {
            pageNumber = Integer.parseInt(pageNumberStr);
        }

        Integer pageSize = PAGE_SIZE;
        String pageSizeStr = request.getParameter("pageSize");
        if (pageSizeStr != null && !"".equals(pageSizeStr)) {
            pageSize = Integer.parseInt(pageSizeStr);
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
        Page<Order> orderList = null;
        JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
        try {
            Member member = memberService.getCurrent();
            if(member == null){
                restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_NO_MEMBER);
                return CommonUtils.returnRestfulResult(callback, restfulResult, jsonConfig);
            }
            Pageable pageable = new Pageable(pageNumber, pageSize);
            orderList = orderService.findPage(member,getOrderStatus(orderStatus),getPaymentStatus(paymentStatus),getShippingStatus(shippingStatus),false,pageable);

            if (orderList != null) {
                for (Order order : orderList.getContent()) {
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
                    if (order.getOrderItems() != null && order.getOrderItems().size() >0) {
                        for (OrderItem orderItem : order.getOrderItems()) {
                            Product product = orderItem.getProduct();
                            if (product!= null) {
                                if (product.getIntroduction() != null){
                                    String introduction = "";
                                    if (product.getIntroduction().indexOf("com") > 0 && product.getIntroduction().indexOf("com") + 3 < product.getIntroduction().length()) {
                                        introduction = product.getIntroduction().substring(product.getIntroduction().indexOf("com") + 3,product.getIntroduction().lastIndexOf("\"") );
                                    }
                                    product.setIntroduction(introduction);
                                }
                                product.initRestEntiy();
                                orderItem.setProduct(product);
                            }
                        }
                    }
                    order.initRestEntityButReturns();
                }
            }

            jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
                public boolean apply(Object obj, String name, Object value) {
                    if(obj instanceof OrderItem && name.equals("order")){
                        return true;
                    }
                    else  if(obj instanceof Product && value == null){//Prodect init
                        return true;
                    }
                    else  if(obj instanceof Product && name.equals("handler")  ){//Prodect init
                        return true;
                    }else  if(obj instanceof Product && name.equals("hibernateLazyInitializer") ){//Prodect init
                        return true;
                    }
                    else  if(obj instanceof Product && name.equals("orderItem")){//Prodect init
                        return true;
                    } else if(obj instanceof Product && name.equals("cartItems")){
                        return true;
                    }else if(obj instanceof Product && name.equals("specifications")){
                        return true;
                    }else if(obj instanceof Product && name.equals("promotions")){
                        return true;
                    }else if(obj instanceof Product && name.equals("tags")){
                        return true;
                    }else if(obj instanceof Product && name.equals("specificationValues")){
                        return true;
                    }else if(obj instanceof Product && name.equals("reviews")){
                        return true;
                    }else if(obj instanceof Product && name.equals("productNotifies")){
                        return true;
                    }else if(obj instanceof Product && name.equals("consultations")){
                        return true;
                    }else if(obj instanceof Product && name.equals("brand")){
                        return true;
                    }else if(obj instanceof Product && name.equals("favoriteMembers")){
                        return true;
                    }else if(obj instanceof Product && name.equals("giftItems")){
                        return true;
                    }else if(obj instanceof Product && name.equals("goods")){
                        return true;
                    }else if(obj instanceof Product && name.equals("memberPrice")){
                        return true;
                    }else if(obj instanceof Product && name.equals("productCategory")){
                        return true;
                    }else if(obj instanceof Product && name.equals("productImages")){
                        return true;
                    }else if(obj instanceof Product && name.equals("area")){
                        return true;
                    }else if(obj instanceof Product && name.equals("parameterValue")){
                        return true;
                    }
                    else if(obj instanceof Member || name.equals("orders")){//Member
                        return true;
                    } else if(obj instanceof Returns && name.equals("order")){//Member
                        return true;
                    } else if(obj instanceof Returns && name.equals("returnsItems")){//returnsItems
                        return true;
                    } else if(obj instanceof Shipping && name.equals("shippingItems")){//shippingItems
                        return true;
                    }  else if(obj instanceof PaymentMethod && name.equals("orders")){//orders
                        return true;
                    } else if(obj instanceof PaymentMethod && name.equals("shippingMethods")){//shippingMethods
                        return true;
                    } else if(obj instanceof PaymentMethod && name.equals("content")){//content
                        return true;
                    } else if(obj instanceof Shipping && name.equals("order")){//order
                        return true;
                    } else if(obj instanceof Order && name.equals("customerOrder")){//customerOrder
                        return true;
                    }  else if(obj instanceof Order && name.equals("commercial")){//commercial
                        return true;
                    }  else if(obj instanceof Order && name.equals("exchangeForOrder")){//exchangeForOrder
                        return true;
                    }  else {
                        return false;
                    }

                }
            });
            jsonConfig.setIgnoreDefaultExcludes(false);  //设置默认忽略

            jsonConfig.setExcludes(new String[]{"handler","hibernateLazyInitializer"});

        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setReturnObj(orderList);
        return CommonUtils.returnRestfulResult(callback, restfulResult, jsonConfig);
    }
    @RequestMapping(value = "/customer_order_list")
    public
    @ResponseBody
    String getCustomerOrderList(Model model, HttpServletRequest request) {
        RestfulResult restfulResult = new RestfulResult();
        String result = "";
        String callback = request.getParameter("callback");

        Integer pageNumber = null;
        String pageNumberStr = request.getParameter("pageNumber");
        if (pageNumberStr != null && !"".equals(pageNumberStr)) {
            pageNumber = Integer.parseInt(pageNumberStr);
        }

        Integer pageSize = PAGE_SIZE;
        String pageSizeStr = request.getParameter("pageSize");
        if (pageSizeStr != null && !"".equals(pageSizeStr)) {
            pageSize = Integer.parseInt(pageSizeStr);
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
        Page<CustomerOrder> orderList = null;
        JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
        try {
            Member member = memberService.getCurrent();
            if(member == null){
                restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_NO_MEMBER);
                return CommonUtils.returnRestfulResult(callback, restfulResult, jsonConfig);
            }
            Pageable pageable = new Pageable(pageNumber, pageSize);
            orderList = customerOrderService.findPage(member,getCustomerOrderStatus(orderStatus),getCustomerPaymentStatus(paymentStatus),getCustomerShippingStatus(shippingStatus),false,pageable);

            if (orderList != null) {
                for(CustomerOrder customerOrder : orderList.getContent()){
                    for (Order order : customerOrder.getRealOrders()) {
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
                        if (order.getOrderItems() != null && order.getOrderItems().size() >0) {
                            for (OrderItem orderItem : order.getOrderItems()) {
                                Product product = orderItem.getProduct();
                                if (product!= null) {
                                    if (product.getIntroduction() != null){
                                        String introduction = "";
                                        if (product.getIntroduction().indexOf("com") > 0 && product.getIntroduction().indexOf("com") + 3 < product.getIntroduction().length()) {
                                            introduction = product.getIntroduction().substring(product.getIntroduction().indexOf("com") + 3,product.getIntroduction().lastIndexOf("\"") );
                                        }
                                        product.setIntroduction(introduction);
                                    }
                                    product.initRestEntiy();
                                    orderItem.setProduct(product);
                                }
                            }
                        }
                        order.initRestEntityButReturns();
                    }
                    if (customerOrder.getMember() != null) {
                        Member newMember = new Member();
                        customerOrder.getMember().initRestEntiy();
                        newMember.setId(customerOrder.getMember().getId());
                        newMember.setIsLocked(customerOrder.getMember().getIsLocked());
                        newMember.setUsername(customerOrder.getMember().getUsername());
                        newMember.initRestEntiy();
                        customerOrder.setMember(newMember);
                    }
                    if (customerOrder.getArea() != null) {
                        customerOrder.setAreaName(customerOrder.getArea().getFullName());
                        customerOrder.setArea(null);
                    }
                    customerOrder.initRestEntity();
                }

            }

            jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
                public boolean apply(Object obj, String name, Object value) {
                    if(obj instanceof OrderItem && name.equals("order")){
                        return true;
                    }
                    else  if(obj instanceof Product && value == null){//Prodect init
                        return true;
                    }
                    else  if(obj instanceof Product && name.equals("handler")  ){//Prodect init
                        return true;
                    }else  if(obj instanceof Product && name.equals("hibernateLazyInitializer") ){//Prodect init
                        return true;
                    }
                    else  if(obj instanceof Product && name.equals("orderItem")){//Prodect init
                        return true;
                    } else if(obj instanceof Product && name.equals("cartItems")){
                        return true;
                    }else if(obj instanceof Product && name.equals("specifications")){
                        return true;
                    }else if(obj instanceof Product && name.equals("promotions")){
                        return true;
                    }else if(obj instanceof Product && name.equals("tags")){
                        return true;
                    }else if(obj instanceof Product && name.equals("specificationValues")){
                        return true;
                    }else if(obj instanceof Product && name.equals("reviews")){
                        return true;
                    }else if(obj instanceof Product && name.equals("productNotifies")){
                        return true;
                    }else if(obj instanceof Product && name.equals("consultations")){
                        return true;
                    }else if(obj instanceof Product && name.equals("brand")){
                        return true;
                    }else if(obj instanceof Product && name.equals("favoriteMembers")){
                        return true;
                    }else if(obj instanceof Product && name.equals("giftItems")){
                        return true;
                    }else if(obj instanceof Product && name.equals("goods")){
                        return true;
                    }else if(obj instanceof Product && name.equals("memberPrice")){
                        return true;
                    }else if(obj instanceof Product && name.equals("productCategory")){
                        return true;
                    }else if(obj instanceof Product && name.equals("productImages")){
                        return true;
                    }else if(obj instanceof Product && name.equals("area")){
                        return true;
                    }else if(obj instanceof Product && name.equals("parameterValue")){
                        return true;
                    }
                    else if(obj instanceof Member || name.equals("orders")){//Member
                        return true;
                    } else if(obj instanceof Returns && name.equals("order")){//Member
                        return true;
                    } else if(obj instanceof Returns && name.equals("returnsItems")){//Member
                        return true;
                    } else if(obj instanceof Shipping && name.equals("shippingItems")){//Member
                        return true;
                    }  else if(obj instanceof PaymentMethod && name.equals("orders")){//PaymentMethod
                        return true;
                    } else if(obj instanceof PaymentMethod && name.equals("shippingMethods")){//PaymentMethod
                        return true;
                    } else if(obj instanceof PaymentMethod && name.equals("content")){//PaymentMethod
                        return true;
                    } else if(obj instanceof Shipping && name.equals("order")){//Member
                        return true;
                    } else if(obj instanceof Order && name.equals("customerOrder")){//Member
                        return true;
                    }  else if(obj instanceof Order && name.equals("")){//Member
                        return true;
                    } else{
                        return false;
                    }

                }
            });
            jsonConfig.setIgnoreDefaultExcludes(false);  //设置默认忽略

            jsonConfig.setExcludes(new String[]{"handler","hibernateLazyInitializer"});

        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setReturnObj(orderList);
        return CommonUtils.returnRestfulResult(callback, restfulResult, jsonConfig);
    }
    @RequestMapping(value = "/find_customer_order")
    public
    @ResponseBody
    String getCustomerOrder(Model model, HttpServletRequest request) {
        RestfulResult restfulResult = new RestfulResult();
        String result = "";
        String callback = request.getParameter("callback");
        String customerOrderSn = request.getParameter("customerOrderSn");

        Integer pageNumber = null;
        String pageNumberStr = request.getParameter("pageNumber");
        if (pageNumberStr != null && !"".equals(pageNumberStr)) {
            pageNumber = Integer.parseInt(pageNumberStr);
        }

        Integer pageSize = PAGE_SIZE;
        String pageSizeStr = request.getParameter("pageSize");
        if (pageSizeStr != null && !"".equals(pageSizeStr)) {
            pageSize = Integer.parseInt(pageSizeStr);
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
        Page<Order> orderList = null;
        JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
        try {
            Member member = memberService.getCurrent();
            if(member == null){
                restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_NO_MEMBER);
                return CommonUtils.returnRestfulResult(callback, restfulResult, jsonConfig);
            }
            Pageable pageable = new Pageable(pageNumber, pageSize);
            Filter filter=new Filter("customerOrder.sn", Filter.Operator.eq,customerOrderSn);
            pageable.getFilters().add(filter);
            orderList = orderService.findPage(member,getOrderStatus(orderStatus),getPaymentStatus(paymentStatus),getShippingStatus(shippingStatus),false,pageable);

            if (orderList != null) {
                for (Order order : orderList.getContent()) {
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
                    if (order.getOrderItems() != null && order.getOrderItems().size() >0) {
                        for (OrderItem orderItem : order.getOrderItems()) {
                            Product product = orderItem.getProduct();
                            if (product!= null) {
                                if (product.getIntroduction() != null){
                                    String introduction = "";
                                    if (product.getIntroduction().indexOf("com") > 0 && product.getIntroduction().indexOf("com") + 3 < product.getIntroduction().length()) {
                                        introduction = product.getIntroduction().substring(product.getIntroduction().indexOf("com") + 3,product.getIntroduction().lastIndexOf("\"") );
                                    }
                                    product.setIntroduction(introduction);
                                }
                                product.initRestEntiy();
                                orderItem.setProduct(product);
                            }
                        }
                    }
                    order.initRestEntityButReturns();
                }
            }

            jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
                public boolean apply(Object obj, String name, Object value) {
                    if(obj instanceof OrderItem && name.equals("order")){
                        return true;
                    }
                    else  if(obj instanceof Product && value == null){//Prodect init
                        return true;
                    }
                    else  if(obj instanceof Product && name.equals("handler")  ){//Prodect init
                        return true;
                    }else  if(obj instanceof Product && name.equals("hibernateLazyInitializer") ){//Prodect init
                        return true;
                    }
                    else  if(obj instanceof Product && name.equals("orderItem")){//Prodect init
                        return true;
                    } else if(obj instanceof Product && name.equals("cartItems")){
                        return true;
                    }else if(obj instanceof Product && name.equals("specifications")){
                        return true;
                    }else if(obj instanceof Product && name.equals("promotions")){
                        return true;
                    }else if(obj instanceof Product && name.equals("tags")){
                        return true;
                    }else if(obj instanceof Product && name.equals("specificationValues")){
                        return true;
                    }else if(obj instanceof Product && name.equals("reviews")){
                        return true;
                    }else if(obj instanceof Product && name.equals("productNotifies")){
                        return true;
                    }else if(obj instanceof Product && name.equals("consultations")){
                        return true;
                    }else if(obj instanceof Product && name.equals("brand")){
                        return true;
                    }else if(obj instanceof Product && name.equals("favoriteMembers")){
                        return true;
                    }else if(obj instanceof Product && name.equals("giftItems")){
                        return true;
                    }else if(obj instanceof Product && name.equals("goods")){
                        return true;
                    }else if(obj instanceof Product && name.equals("memberPrice")){
                        return true;
                    }else if(obj instanceof Product && name.equals("productCategory")){
                        return true;
                    }else if(obj instanceof Product && name.equals("productImages")){
                        return true;
                    }else if(obj instanceof Product && name.equals("area")){
                        return true;
                    }else if(obj instanceof Product && name.equals("parameterValue")){
                        return true;
                    }
                    else if(obj instanceof Member || name.equals("orders")){//Member
                        return true;
                    } else if(obj instanceof Returns && name.equals("order")){//Member
                        return true;
                    } else if(obj instanceof Returns && name.equals("returnsItems")){//Member
                        return true;
                    } else if(obj instanceof Shipping && name.equals("shippingItems")){//Member
                        return true;
                    }  else if(obj instanceof PaymentMethod && name.equals("orders")){//PaymentMethod
                        return true;
                    } else if(obj instanceof PaymentMethod && name.equals("shippingMethods")){//PaymentMethod
                        return true;
                    } else if(obj instanceof PaymentMethod && name.equals("content")){//PaymentMethod
                        return true;
                    } else if(obj instanceof Shipping && name.equals("order")){//Member
                        return true;
                    } else if(obj instanceof Order && name.equals("customerOrder")){//Member
                        return true;
                    }  else if(obj instanceof Order && name.equals("commercial")){//Member
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

        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setReturnObj(orderList);
        return CommonUtils.returnRestfulResult(callback, restfulResult, jsonConfig);
    }
    //order_comfirm
    @RequestMapping(value = "/order_comfirm")
    public
    @ResponseBody
    String confirmOrder(Model model, HttpServletRequest request) {
        String result = "";
        String callback = request.getParameter("callback");

        String orderSn = request.getParameter("orderSn");

        try {
            Order order = orderService.findBySn(orderSn);
            order.setOrderStatus(Order.OrderStatus.completed);
            order.setCompletedDate(new Date());
            orderService.save(order);
        } catch (Exception e) {
            RestfulResult restfulResult = new RestfulResult();
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }

        RestfulResult restfulResult = new RestfulResult();
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult("已确认收货！");
        return returnRestfulResult(callback, restfulResult);
    }

    //order_del
    @RequestMapping(value = "/order_del")
    public
    @ResponseBody
    String deleteOrder(Model model, HttpServletRequest request) {
        String result = "";
        String callback = request.getParameter("callback");

        String orderSn = request.getParameter("orderSn");

        try {
            Order order = orderService.findBySn(orderSn);
            order.setOrderStatus(Order.OrderStatus.deleted);
            orderService.update(order);
        } catch (Exception e) {
            RestfulResult restfulResult = new RestfulResult();
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }

        RestfulResult restfulResult = new RestfulResult();
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult("delete order sucess!");
        return returnRestfulResult(callback, restfulResult);
    }

    //order_cancel
    @RequestMapping(value = "/order_cancel")
    public
    @ResponseBody
    String cancelOrder(Model model, HttpServletRequest request) {
        String result = "";
        String callback = request.getParameter("callback");

        String orderSn = request.getParameter("orderSn");

        try {
            Order order = orderService.findBySn(orderSn);
            orderService.cancel(order, null);
        } catch (Exception e) {
            RestfulResult restfulResult = new RestfulResult();
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }

        RestfulResult restfulResult = new RestfulResult();
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult(Message.success("shop.message.success").getContent());
        return returnRestfulResult(callback, restfulResult);
    }

    //order_comfirm
    @RequestMapping(value = "/return_apply")
    public
    @ResponseBody
    String returnOrder(Model model, HttpServletRequest request) {
        String result = "";
        String callback = request.getParameter("callback");

        String orderSn = request.getParameter("orderSn");

        try {
            Order order = orderService.findBySn(orderSn);
            orderService.cancel(order, null);
        } catch (Exception e) {
            RestfulResult restfulResult = new RestfulResult();
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }

        JSONObject jb = JSONObject.fromObject("{\"result\":\"sucess\"}");
        result = jb.toString();
        result = Message.success("shop.message.success").getContent();
        if (callback == null) {
            return result;
        } else {
            return callback + "('" + result + "')";
        }
    }

    @RequestMapping(value = "/order_view")
    public
    @ResponseBody
    String viewOrderDetail(Model model, HttpServletRequest request) {
        RestfulResult restfulResult = new RestfulResult();
        String result = "";
        String callback = request.getParameter("callback");

        String orderSn = request.getParameter("orderSn");

        String orderId = request.getParameter("orderId");
        JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
        try {
            Order order = null;
            if(orderSn!=null && !"".equals(orderSn)){
                order = orderService.findBySn( orderSn );
            } else {
                if(orderId!=null && !"".equals(orderId)){
                    order = orderService.find(Long.parseLong(orderId));
                }
            }
            if (order != null) {
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
                if (order.getOrderItems() != null && order.getOrderItems().size() >0) {
                    for (OrderItem orderItem : order.getOrderItems()) {
                        Product product = orderItem.getProduct();
                        if (product.getIntroduction() != null){
                            String introduction = "";
                            if (product.getIntroduction().indexOf("com") > 0 && product.getIntroduction().indexOf("com") + 3 < product.getIntroduction().length()) {
                                introduction = product.getIntroduction().substring(product.getIntroduction().indexOf("com") + 3,product.getIntroduction().lastIndexOf("\"") );
                            }
                            product.setIntroduction(introduction);
                        }
                        product.initRestEntiy();
                        orderItem.setProduct(product);
                    }
                }
                if(order.getExchangeForOrder() != null ){
                   order.getExchangeForOrder().initRestExchangeEntity();
                }
                order.initRestEntityButReturns();
            }

            jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
                public boolean apply(Object obj, String name, Object value) {
                    if(obj instanceof OrderItem && name.equals("order")){
                        return true;
                    }
                    else  if(obj instanceof Product && value == null){//Prodect init
                        return true;
                    }
                    else  if(obj instanceof Product && name.equals("handler")  ){//Prodect init
                        return true;
                    }else  if(obj instanceof Product && name.equals("hibernateLazyInitializer") ){//Prodect init
                        return true;
                    }
                    else  if(obj instanceof Product && name.equals("orderItem")){//Prodect init
                        return true;
                    } else if(obj instanceof Product && name.equals("cartItems")){
                        return true;
                    }else if(obj instanceof Product && name.equals("specifications")){
                        return true;
                    }else if(obj instanceof Product && name.equals("promotions")){
                        return true;
                    }else if(obj instanceof Product && name.equals("tags")){
                        return true;
                    }else if(obj instanceof Product && name.equals("specificationValues")){
                        return true;
                    }else if(obj instanceof Product && name.equals("reviews")){
                        return true;
                    }else if(obj instanceof Product && name.equals("productNotifies")){
                        return true;
                    }else if(obj instanceof Product && name.equals("consultations")){
                        return true;
                    }else if(obj instanceof Product && name.equals("brand")){
                        return true;
                    }else if(obj instanceof Product && name.equals("favoriteMembers")){
                        return true;
                    }else if(obj instanceof Product && name.equals("giftItems")){
                        return true;
                    }else if(obj instanceof Product && name.equals("goods")){
                        return true;
                    }else if(obj instanceof Product && name.equals("memberPrice")){
                        return true;
                    }else if(obj instanceof Product && name.equals("productCategory")){
                        return true;
                    }else if(obj instanceof Product && name.equals("productImages")){
                        return true;
                    }else if(obj instanceof Product && name.equals("area")){
                        return true;
                    }else if(obj instanceof Product && name.equals("parameterValue")){
                        return true;
                    }
                    else if(obj instanceof Member || name.equals("orders")){//Member
                        return true;
                    } else if(obj instanceof Returns && name.equals("order")){//Member
                        return true;
                    } else if(obj instanceof Returns && name.equals("returnsItems")){//Member
                        return true;
                    } else if(obj instanceof Shipping && name.equals("shippingItems")){//Member
                        return true;
                    }  else if(obj instanceof PaymentMethod && name.equals("orders")){//PaymentMethod
                        return true;
                    } else if(obj instanceof PaymentMethod && name.equals("shippingMethods")){//PaymentMethod
                        return true;
                    } else if(obj instanceof PaymentMethod && name.equals("content")){//PaymentMethod
                        return true;
                    } else if(obj instanceof Shipping && name.equals("order")){//Member
                        return true;
                    } else if(obj instanceof Order && name.equals("customerOrder")){//Member
                        return true;
                    }  else if(obj instanceof Returns && name.equals("exchangeOrder")){//exchangeForOrder
                        return true;
                    } else if(obj instanceof Returns && name.equals("order")){// Returns order
                        return true;
                    }  else if(obj instanceof ReturnsItem && name.equals("returns")){// ReturnsItem returns
                        return true;
                    } else {
                        return false;
                    }

                }
            });
            jsonConfig.setIgnoreDefaultExcludes(false);  //设置默认忽略

            jsonConfig.setExcludes(new String[]{"handler", "hibernateLazyInitializer"});
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
            restfulResult.setReturnObj( order );
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
       return CommonUtils.returnRestfulResult(callback, restfulResult, jsonConfig);
    }

    protected boolean isValid(Object target, Class<?>... groups) {
        return true;
    }

    private String returnResult(String callback, String result) {
        RestfulResult restfulResult = new RestfulResult();
        restfulResult.setResult(result);
       return returnRestfulResult(callback, restfulResult);
    }

    private String returnRestfulResult(String callback, RestfulResult restfulResult) {
        JSONArray jb = JSONArray.fromObject(restfulResult);
        String result = jb.toString();
        if (callback == null) {
            return result;
        } else {
            return callback + "('" + result + "')";
        }
    }

    private CustomerOrder.OrderStatus getCustomerOrderStatus(int status){
        switch (status){
            case 0 : return CustomerOrder.OrderStatus.unconfirmed;
            case 1 : return CustomerOrder.OrderStatus.confirmed;
            case 2 : return CustomerOrder.OrderStatus.completed;
            case 3 : return CustomerOrder.OrderStatus.cancelled;
            case 4 : return CustomerOrder.OrderStatus.deleted;
            default:return null;
        }
    }

    private CustomerOrder.PaymentStatus getCustomerPaymentStatus(int status){
        switch (status){
            case 0 : return CustomerOrder.PaymentStatus.unpaid;
            case 1 : return CustomerOrder.PaymentStatus.partialPayment;
            case 2 : return CustomerOrder.PaymentStatus.paid;
            case 3 : return CustomerOrder.PaymentStatus.partialRefunds;
            case 4 : return CustomerOrder.PaymentStatus.refunded;
            default:return null;
        }
    }
    private CustomerOrder.ShippingStatus getCustomerShippingStatus(int status){
        switch (status){
            case 0 : return CustomerOrder.ShippingStatus.unshipped;
            case 1 : return CustomerOrder.ShippingStatus.partialShipment;
            case 2 : return CustomerOrder.ShippingStatus.shipped;
            case 3 : return CustomerOrder.ShippingStatus.partialReturns;
            case 4 : return CustomerOrder.ShippingStatus.returned;
            default:return null;
        }
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

    private CustomerOrder generateCustomerOrder(CustomerOrder customerOrder,Order order){
        customerOrder.setAddress(order.getAddress());
        customerOrder.setAmountPaid(order.getAmountPaid());
        customerOrder.setArea(order.getArea());
        customerOrder.setAreaName(order.getAreaName());
        customerOrder.setConsignee(order.getConsignee());
        customerOrder.setExpire(order.getExpire());
        customerOrder.setFee(order.getFee());
        customerOrder.setFreight(order.getFreight());
        customerOrder.setMember(order.getMember());
        customerOrder.setMemo(order.getMemo());
        customerOrder.setOrderStatus(CustomerOrder.OrderStatus.unconfirmed);
        customerOrder.setPaymentStatus(CustomerOrder.PaymentStatus.unpaid);
        customerOrder.setShippingStatus(CustomerOrder.ShippingStatus.unshipped);
        return customerOrder;
    }
}
