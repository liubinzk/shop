package com.jqb.shop.restful.controller;

import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.entity.*;
import com.jqb.shop.plugin.wxpay.api.CommonUtils;
import com.jqb.shop.restful.entity.RestfulConstants;
import com.jqb.shop.restful.entity.RestfulResult;
import com.jqb.shop.restful.entity.RestfulReturns;
import com.jqb.shop.service.*;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Controller
@RequestMapping(value="rest")
public class ReturnsRestController {

    /**
     * Logging for this instance
     */
    private org.apache.commons.logging.Log log = LogFactory.getLog(ReturnsRestController.class);

    @Resource(name = "orderServiceImpl")
    private OrderService orderService;
    @Resource(name = "orderItemServiceImpl")
    private OrderItemService orderItemService;
    @Resource(name = "shippingMethodServiceImpl")
    private ShippingMethodService shippingMethodService;
    @Resource(name = "deliveryCorpServiceImpl")
    private DeliveryCorpService deliveryCorpService;
    @Resource(name = "areaServiceImpl")
    private AreaService areaService;
    @Resource(name = "adminServiceImpl")
    private AdminService adminService;
    @Resource(name = "snServiceImpl")
    private SnService snService;
    @Resource(name = "returnsServiceImpl")
    private ReturnsService returnsService;
    @Resource(name = "productServiceImpl")
    private ProductService productService;
    @Resource(name = "memberServiceImpl")
    private MemberService memberService;




    @RequestMapping(value = "/list_order_returns")
    public
    @ResponseBody
    String processReturn(Model model, HttpServletRequest request) {
        RestfulResult restfulResult = new RestfulResult();
        List<RestfulReturns> restfulReturnsList = new ArrayList<RestfulReturns>();
        String callback = request.getParameter("callback");

        String orderIdstr = request.getParameter("orderId");


        long orderId = 0;
        if (orderIdstr != null) {
            orderId = Long.parseLong(orderIdstr);
        }

        List<Returns> returnsList = null;
        try {
            returnsList = returnsService.findByOrderId(orderId);
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
        jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
            public boolean apply(Object obj, String name, Object value) {
                if (obj instanceof Returns && name.equals("handler")) {//Prodect init
                    return true;
                } else if (obj instanceof Returns && name.equals("order")) {//Prodect init
                    return true;
                } else if (obj instanceof Returns && name.equals("exchangeOrder")) {//Prodect init
                    return true;
                } else if (obj instanceof ReturnsItem && name.equals("returns")) {//Prodect init
                    return true;
                } else {
                    return false;
                }
            }
        });
        if(returnsList != null && returnsList.size()>0) {
            for (Returns returns : returnsList) {
                RestfulReturns restfulReturns = new RestfulReturns();
                //product
                Product product = null;
                if(returns.getReturnsItems() != null && returns.getReturnsItems().size()>0){
                    List<ReturnsItem> returnsItemList_new = new ArrayList<ReturnsItem>();
                    for (ReturnsItem returnsItem : returns.getReturnsItems()){
                        if(returnsItem != null && !"".equals(returnsItem) ){
                            ReturnsItem returnsItem_new = new ReturnsItem();
                            returnsItem_new.setCost( returnsItem.getCost() );
                            returnsItem_new.setName( returnsItem.getName() );
                            returnsItem_new.setSn(returnsItem.getSn());
                            returnsItem_new.setPrice(returnsItem.getPrice());
                            returnsItem_new.setQuantity(returnsItem.getQuantity());
                            returnsItemList_new.add(returnsItem_new);
                            product = productService.findBySn(returnsItem.getSn());
                            if (product.getArea() != null) {
                                product.setAreaName(product.getArea().getFullName());
                                product.setArea(null);
                            }
                            //introduction
                            if (product.getIntroduction() != null){
                                String introduction = "";
                                if (product.getIntroduction().indexOf("com") > 0 && product.getIntroduction().indexOf("com") + 3 < product.getIntroduction().length()) {
                                    introduction = product.getIntroduction().substring(product.getIntroduction().indexOf("com") + 3,product.getIntroduction().lastIndexOf("\"") );
                                }
                                product.setIntroduction(introduction);
                            }
                            List<Product> siblings =  product.getSiblings();
                            if (siblings != null && siblings.size() > 0 ) {
                                for (Product sblingProduct : siblings) {

                                    if (sblingProduct.getArea() != null) {
                                        sblingProduct.setAreaName(sblingProduct.getArea().getFullName());
                                        sblingProduct.setArea(null);
                                    }
                                    //introduction
                                    if (sblingProduct.getIntroduction() != null){
                                        String introduction = "";
                                        if (sblingProduct.getIntroduction().indexOf("com") > 0 && sblingProduct.getIntroduction().indexOf("com") + 3 < sblingProduct.getIntroduction().length()) {
                                            introduction = sblingProduct.getIntroduction().substring(sblingProduct.getIntroduction().indexOf("com") + 3,sblingProduct.getIntroduction().lastIndexOf("\"") );
                                        }
                                        sblingProduct.setIntroduction(introduction);
                                    }
                                    sblingProduct.setPromotions(null);
                                    sblingProduct.setBrand(null);
                                    sblingProduct.setCartItems(null);
                                    sblingProduct.setTags(null);
                                    sblingProduct.setCartItems(null);
                                    sblingProduct.setProductCategory(null);
                                    sblingProduct.setConsultations(null);
                                    sblingProduct.setFavoriteMembers(null);
                                    sblingProduct.setGiftItems(null);
                                    sblingProduct.setGoods(null);
                                    sblingProduct.setMemberPrice(null);
                                    sblingProduct.setOrderItems(null);
                                    sblingProduct.setParameterValue(null);
                                    sblingProduct.setProductImages(null);
                                    sblingProduct.setProductNotifies(null);
                                    sblingProduct.setReviews(null);
                                    sblingProduct.setProductImages(null);
                                    sblingProduct.setSpecificationValues(null);
                                    sblingProduct.setSpecifications(null);
                                    sblingProduct.initRestEntiy();
                                }
                            }

                            product.setPromotions(null);
                            product.setBrand(null);
                            product.setCartItems(null);
                            product.setTags(null);
                            product.setCartItems(null);
                            product.setProductCategory(null);
                            product.setConsultations(null);
                            product.setFavoriteMembers(null);
                            product.setGiftItems(null);
                            product.setGoods(null);
                            product.setMemberPrice(null);
                            product.setOrderItems(null);
                            product.setParameterValue(null);
                            //            product.setProductImages(null);
                            product.setProductNotifies(null);
                            product.setReviews(null);
                            product.setSpecificationValues(null);
                            product.setSpecifications(null);
                            restfulReturns.setReturns(returns);
                            restfulReturns.getProducts().add(product);
                            restfulReturnsList.add(restfulReturns);
                        }
                    }
                    returns.setReturnsItems( returnsItemList_new );
                }
            }
        }
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult("return success!");
        restfulResult.setReturnObj(restfulReturnsList);
        return com.jqb.shop.util.CommonUtils.returnRestfulResult( callback, restfulResult, jsonConfig);
    }

    @RequestMapping(value = "/list_returns")
    public
    @ResponseBody
    String getReturnList(Model model, HttpServletRequest request) {
        RestfulResult restfulResult = new RestfulResult();
        List<RestfulReturns> restfulReturnsList = new ArrayList<RestfulReturns>();
        String callback = request.getParameter("callback");

        String pageNumberStr = request.getParameter("pageNumber");
        String pageSizeStr = request.getParameter("pageSize");

        Integer pageNumber = 1;
        if (pageNumberStr != null && !"".equals(pageNumberStr)) {
            pageNumber = Integer.parseInt(pageNumberStr);
        }
        Integer pageSize = 10;
        if (pageSizeStr != null && !"".equals(pageSizeStr)) {
            pageSize = Integer.parseInt( pageSizeStr );
        }

        Member member = memberService.getCurrent();
        Page<Returns> returnsPage = null;
        Pageable pageable = new Pageable(pageNumber, pageSize);
        try {
            returnsPage = returnsService.findPage(pageable, member);
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
        jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
            public boolean apply(Object obj, String name, Object value) {
                if (obj instanceof Returns && name.equals("handler")) {//Prodect init
                    return true;
                }  else if(obj instanceof Product && name.equals("area")){
                    return true;
                }  else if (obj instanceof Product && name.equals("commercial")) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("productImages")) {//Prodect init
                    return true;
                } else if (obj instanceof Returns && name.equals("order")) {//Prodect init
                    return true;
                }  else if (obj instanceof ReturnsItem && name.equals("returns")) {//Prodect init
                    return true;
                } else if (obj instanceof ReturnsItem && name.equals("subtotal")) {//Prodect init
                    return true;
                } else if (obj instanceof Returns && name.equals("exchangeOrder")) {//exchangeOrder init
                    return true;
                }  else {
                    return false;
                }
            }
        });
        jsonConfig.setExcludes(new String[]{"handler", "hibernateLazyInitializer"});
        if(returnsPage != null && returnsPage.getContent().size()>0) {

            for (Returns returns : returnsPage.getContent()) {
                RestfulReturns restfulReturns = new RestfulReturns();
                restfulReturns.setOrderId(returns.getOrder().getId());
                //product
                Product product = null;
                List<ReturnsItem> returnsItemList_new = new ArrayList<ReturnsItem>();
                if(returns.getReturnsItems() != null && returns.getReturnsItems().size()>0){
                    for (ReturnsItem returnsItem : returns.getReturnsItems()){
                        if(returnsItem != null && !"".equals(returnsItem) ){
                            ReturnsItem returnsItem_new = new ReturnsItem();
                            returnsItem_new.setCost( returnsItem.getCost() );
                            returnsItem_new.setName( returnsItem.getName() );
                            returnsItem_new.setSn(returnsItem.getSn());
                            returnsItem_new.setPrice(returnsItem.getPrice());
                            returnsItem_new.setQuantity(returnsItem.getQuantity());
                            returnsItemList_new.add(returnsItem_new);
                            product = productService.findBySn(returnsItem.getSn());
                            if(product == null){
                                break;
                            }
                            if ( product.getArea() != null) {
                                product.setAreaName(product.getArea().getFullName());
                                product.setArea(null);
                            }
                            //introduction
                            if (product.getIntroduction() != null){
                                String introduction = "";
                                if (product.getIntroduction().indexOf("com") > 0 && product.getIntroduction().indexOf("com") + 3 < product.getIntroduction().length()) {
                                    introduction = product.getIntroduction().substring(product.getIntroduction().indexOf("com") + 3,product.getIntroduction().lastIndexOf("\"") );
                                }
                                product.setIntroduction(introduction);
                            }
                            List<Product> siblings =  product.getSiblings();
                            if (siblings != null && siblings.size() > 0 ) {
                                for (Product sblingProduct : siblings) {

                                    if (sblingProduct.getArea() != null) {
                                        sblingProduct.setAreaName(sblingProduct.getArea().getFullName());
                                        sblingProduct.setArea(null);
                                    }
                                    //introduction
                                    if (sblingProduct.getIntroduction() != null){
                                        String introduction = "";
                                        if (sblingProduct.getIntroduction().indexOf("com") > 0 && sblingProduct.getIntroduction().indexOf("com") + 3 < sblingProduct.getIntroduction().length()) {
                                            introduction = sblingProduct.getIntroduction().substring(sblingProduct.getIntroduction().indexOf("com") + 3,sblingProduct.getIntroduction().lastIndexOf("\"") );
                                        }
                                        sblingProduct.setIntroduction(introduction);
                                    }
                                    sblingProduct.setPromotions(null);
                                    sblingProduct.setBrand(null);
                                    sblingProduct.setCartItems(null);
                                    sblingProduct.setTags(null);
                                    sblingProduct.setCartItems(null);
                                    sblingProduct.setProductCategory(null);
                                    sblingProduct.setConsultations(null);
                                    sblingProduct.setFavoriteMembers(null);
                                    sblingProduct.setGiftItems(null);
                                    sblingProduct.setGoods(null);
                                    sblingProduct.setMemberPrice(null);
                                    sblingProduct.setOrderItems(null);
                                    sblingProduct.setParameterValue(null);
                                    sblingProduct.setProductImages(null);
                                    sblingProduct.setProductNotifies(null);
                                    sblingProduct.setReviews(null);
                                    sblingProduct.setProductImages(null);
                                    sblingProduct.setSpecificationValues(null);
                                    sblingProduct.setSpecifications(null);
                                    sblingProduct.setCommercial(null);
                                    sblingProduct.setSiblings(null);
                                    sblingProduct.initRestEntiy();
                                }
                            }

                            product.setPromotions(null);
                            product.setBrand(null);
                            product.setCartItems(null);
                            product.setTags(null);
                            product.setCartItems(null);
                            product.setProductCategory(null);
                            product.setConsultations(null);
                            product.setFavoriteMembers(null);
                            product.setGiftItems(null);
                            product.setGoods(null);
                            product.setMemberPrice(null);
                            product.setOrderItems(null);
                            product.setParameterValue(null);
                            product.setIntroduction(null);
                            //            product.setProductImages(null);
                            product.setProductNotifies(null);
                            product.setReviews(null);
                            product.setSpecificationValues(null);
                            product.setSpecifications(null);
                        }
                    }
                }
                returns.setReturnsItems( returnsItemList_new );
                restfulReturns.setReturns(returns);
                restfulReturns.getProducts().add(product);
                restfulReturnsList.add(restfulReturns);
            }
        }
        Page<RestfulReturns> restReturnsPage = new Page<RestfulReturns>(restfulReturnsList,returnsPage.getTotal(),pageable);
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult("return success!");
        restfulResult.setReturnObj(restReturnsPage);
        return com.jqb.shop.util.CommonUtils.returnRestfulResult( callback, restfulResult, jsonConfig);
    }

    @RequestMapping("/return_orderItem")
    public @ResponseBody  String makeReturns(HttpServletRequest request) {
        Returns returns = new Returns();
        RestfulResult restfulResult = new RestfulResult();
        String callback = request.getParameter("callback");

        String orderIdstr = request.getParameter("orderId");
        String areaIdstr = request.getParameter("areaId");
        String orderItemSn = request.getParameter("orderItemSn");
        String orderItemId = request.getParameter("orderItemId");
        String returnNumStr = request.getParameter("returnNum");
        String memo = request.getParameter("memo");
        String serviceTypeStr = request.getParameter("serviceType");

        int serviceType = 0;
        if (serviceTypeStr != null) {
            serviceType = Integer.parseInt(serviceTypeStr);
        }

        long orderId = 0;
        if (orderIdstr != null) {
            orderId = Long.parseLong(orderIdstr);
        }
        try {
            Order order = orderService.find(orderId);
            if (order == null) {
                restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_FAIL);
                restfulResult.setResult(com.jqb.shop.Message.error("admin.message.error").getContent());
                return returnRestfulResult( callback, restfulResult );
            }
            ReturnsItem returnsItem = new ReturnsItem();
            int returnNum = Integer.parseInt(returnNumStr);
            OrderItem orderItem = order.getOrderItem(orderItemSn);
            if (orderItem == null || orderItem.getShippedQuantity() - returnNum < 0) {
                restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_FAIL);
                restfulResult.setResult("未发货");
                return returnRestfulResult( callback, restfulResult );
            }
            returnsItem.setName(orderItem.getFullName());
            returnsItem.setReturns(returns);
            returnsItem.setQuantity(returnNum);
            returnsItem.setSn(orderItem.getSn());
            //commercial 结算
            returnsItem.setPrice(orderItem.getPrice());
            returnsItem.setCost(orderItem.getCost());
            List<ReturnsItem> returnsItemList = new ArrayList<ReturnsItem>();
            returnsItemList.add(returnsItem);
            returns.setReturnsItems(returnsItemList);
            returns.setOrder(order);
            returns.setServiceType(serviceType);
            returns.setReturnsStatus(Returns.ReturnsStatus.unconfirmed);
//        ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
//        returns.setShippingMethod(shippingMethod != null ? shippingMethod.getName() : null);
//        DeliveryCorp deliveryCorp = deliveryCorpService.find(deliveryCorpId);
//        returns.setDeliveryCorp(deliveryCorp != null ? deliveryCorp.getName() : null);
            long areaId = 0;
            if (areaIdstr != null) {
                areaId = Long.parseLong(areaIdstr);
            }
            Area area = areaService.find(areaId);
            returns.setArea(area != null ? area.getFullName() : null);
            if (order.isExpired() || order.getOrderStatus() != Order.OrderStatus.completed) {
                restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_FAIL);
                restfulResult.setResult("orderStatus not valide: " + order.getOrderStatus());
                return returnRestfulResult( callback, restfulResult );
            }
//            if (order.getShippingStatus() != Order.ShippingStatus.shipped && order.getShippingStatus() != Order.ShippingStatus.partialShipment && order.getShippingStatus() != Order.ShippingStatus.partialReturns) {
//
//            } order包含多个订单时 不能通过order的shippingStatus判断该orderItem是否退换货

            Returns checked_returns = returnsService.findByOrderIdAndItemId(orderId, orderItem.getProduct().getSn());
            if(checked_returns != null && !"".equals(checked_returns)){
                restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_FAIL);
                if( order.getShippingStatus() == Order.ShippingStatus.returned ){
                    restfulResult.setResult("此订单已申请售后服务。");
                } else {
                    restfulResult.setResult("ship status not valide: " + order.getShippingStatus());
                }
                return returnRestfulResult( callback, restfulResult );
            }
            returns.setSn(snService.generate(Sn.Type.returns));
            returns.setMemo(memo);
            orderService.returns(order, returns, null);
        } catch (Exception e) {
            restfulResult = new RestfulResult();
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult("return success!");
        return returnRestfulResult(callback, restfulResult);
    }
    @RequestMapping("/update_returnsItem")
    public @ResponseBody  String updateReturns(HttpServletRequest request) {
        Returns returns = new Returns();
        RestfulResult restfulResult = new RestfulResult();
        String callback = request.getParameter("callback");

        String orderIdstr = request.getParameter("orderId");
        String areaIdstr = request.getParameter("areaId");
        String productItemSn = request.getParameter("productItemSn");
        String orderItemId = request.getParameter("orderItemId");
        String returnNumStr = request.getParameter("returnNum");
        String shipMethod = request.getParameter("shipMethod");
        String trackingNo = request.getParameter("trackingNo");
        String customerContactName = request.getParameter("customerContactName");
        String customerMobilePhone = request.getParameter("customerMobilePhone");
        String customerContactAddress = request.getParameter("customerContactAddress");



        long orderId = 0;
        if (orderIdstr != null) {
            orderId = Long.parseLong(orderIdstr);
        } else {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_ORDER_FAIL);
            restfulResult.setResult("订单ID为空。");
            return returnRestfulResult(callback, restfulResult);
        }
        try {
            returns = returnsService.findByOrderIdAndItemId(orderId, productItemSn);
            returns.setShipper(customerContactName);
            returns.setPhone(customerMobilePhone);
            returns.setShippingMethod(shipMethod);
            returns.setTrackingNo(trackingNo);
            returns.setAddress(customerContactAddress);
            returnsService.update(returns);
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult("return success!");
        return returnRestfulResult(callback, restfulResult);
    }

    @RequestMapping(value = "/get_returns_item")
    public
    @ResponseBody
    String getReturnItem(Model model, HttpServletRequest request) {
        RestfulResult restfulResult = new RestfulResult();
        RestfulReturns restfulReturns = new RestfulReturns();
        String callback = request.getParameter("callback");

        String orderIdstr = request.getParameter("orderId");
        String productItemSn = request.getParameter("productItemSn");
        String orderItemId = request.getParameter("orderItemId");


        long orderId = 0;
        if (orderIdstr != null) {
            orderId = Long.parseLong(orderIdstr);
        } else {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_FAIL);
            restfulResult.setResult("orderId is null.");
            return returnRestfulResult(callback, restfulResult);
        }
        Returns returns = null;
        try {
            returns = returnsService.findByOrderIdAndItemId(orderId, productItemSn);
            //product
            Product product = null;
            if(returns.getReturnsItems() != null && returns.getReturnsItems().size()>0){
                List<ReturnsItem> returnsItemList_new = new ArrayList<ReturnsItem>();
                for (ReturnsItem returnsItem : returns.getReturnsItems()){
                    if(returnsItem != null && !"".equals(returnsItem) ){
                        ReturnsItem returnsItem_new = new ReturnsItem();
                        returnsItem_new.setCost( returnsItem.getCost() );
                        returnsItem_new.setName( returnsItem.getName() );
                        returnsItem_new.setSn(returnsItem.getSn());
                        returnsItem_new.setPrice(returnsItem.getPrice());
                        returnsItem_new.setQuantity(returnsItem.getQuantity());
                        returnsItemList_new.add(returnsItem_new);
                        product = productService.findBySn(returnsItem.getSn());
                        if (product.getArea() != null) {
                            product.setAreaName(product.getArea().getFullName());
                            product.setArea(null);
                        }
                        //introduction
                        if (product.getIntroduction() != null){
                            String introduction = "";
                            if (product.getIntroduction().indexOf("com") > 0 && product.getIntroduction().indexOf("com") + 3 < product.getIntroduction().length()) {
                                introduction = product.getIntroduction().substring(product.getIntroduction().indexOf("com") + 3,product.getIntroduction().lastIndexOf("\"") );
                            }
                            product.setIntroduction(introduction);
                        }
                        List<Product> siblings =  product.getSiblings();
                        if (siblings != null && siblings.size() > 0 ) {
                            for (Product sblingProduct : siblings) {

                                if (sblingProduct.getArea() != null) {
                                    sblingProduct.setAreaName(sblingProduct.getArea().getFullName());
                                    sblingProduct.setArea(null);
                                }
                                //introduction
                                if (sblingProduct.getIntroduction() != null){
                                    String introduction = "";
                                    if (sblingProduct.getIntroduction().indexOf("com") > 0 && sblingProduct.getIntroduction().indexOf("com") + 3 < sblingProduct.getIntroduction().length()) {
                                        introduction = sblingProduct.getIntroduction().substring(sblingProduct.getIntroduction().indexOf("com") + 3,sblingProduct.getIntroduction().lastIndexOf("\"") );
                                    }
                                    sblingProduct.setIntroduction(introduction);
                                }
                                sblingProduct.setPromotions(null);
                                sblingProduct.setBrand(null);
                                sblingProduct.setCartItems(null);
                                sblingProduct.setTags(null);
                                sblingProduct.setCartItems(null);
                                sblingProduct.setProductCategory(null);
                                sblingProduct.setConsultations(null);
                                sblingProduct.setFavoriteMembers(null);
                                sblingProduct.setGiftItems(null);
                                sblingProduct.setGoods(null);
                                sblingProduct.setMemberPrice(null);
                                sblingProduct.setOrderItems(null);
                                sblingProduct.setParameterValue(null);
                                sblingProduct.setProductImages(null);
                                sblingProduct.setProductNotifies(null);
                                sblingProduct.setReviews(null);
                                sblingProduct.setProductImages(null);
                                sblingProduct.setSpecificationValues(null);
                                sblingProduct.setSpecifications(null);
                                sblingProduct.initRestEntiy();
                            }
                        }

                        product.setPromotions(null);
                        product.setBrand(null);
                        product.setCartItems(null);
                        product.setTags(null);
                        product.setCartItems(null);
                        product.setProductCategory(null);
                        product.setConsultations(null);
                        product.setFavoriteMembers(null);
                        product.setGiftItems(null);
                        product.setGoods(null);
                        product.setMemberPrice(null);
                        product.setOrderItems(null);
                        product.setParameterValue(null);
                        //            product.setProductImages(null);
                        product.setProductNotifies(null);
                        product.setReviews(null);
                        product.setSpecificationValues(null);
                        product.setSpecifications(null);

                        restfulReturns.getProducts().add(product);
                    }
                }
                returns.setReturnsItems( returnsItemList_new );
            }
            if(returns.getReturnsStatus()==Returns.ReturnsStatus.confirmed && returns.getOperator() != null){
                Admin admin = adminService.findByUsername(returns.getOperator());
                AdminReceiver receiver = null;
                if (admin.getReceivers() != null && admin.getReceivers().size() > 0) {
                    for (AdminReceiver receiverElem : admin.getReceivers()) {
                        if (receiverElem.getIsDefault()) {
                            receiver = receiverElem;
                            break;
                        }
                    }
                }


                restfulReturns.setReceiver(receiver);
            }
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
        jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
            public boolean apply(Object obj, String name, Object value) {
                if (obj instanceof Returns && name.equals("handler")) {//Prodect init
                    return true;
                } else if (obj instanceof Returns && name.equals("order")) {//Prodect init
                    return true;
                } else if (obj instanceof Returns && name.equals("exchangeOrder")) {//Prodect init
                    return true;
                }  else if (obj instanceof ReturnsItem && name.equals("returns")) {//Prodect init
                    return true;
                } else if (obj instanceof AdminReceiver && name.equals("admin")) {//Prodect init
                    return true;
                }  else if (obj instanceof AdminReceiver && name.equals("area")) {//Prodect init
                    return true;
                } else {
                    return false;
                }
            }
        });
        jsonConfig.setExcludes(new String[]{"handler", "hibernateLazyInitializer"});
        restfulReturns.setOrderId(orderId);
        restfulReturns.setReturns(returns);
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult("return success!");
        restfulResult.setReturnObj(restfulReturns);
        return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback, restfulResult, jsonConfig);
    }

    @RequestMapping(value = "/check_returns_item")
    public
    @ResponseBody
    String checkReturnItem(Model model, HttpServletRequest request) {
        RestfulResult restfulResult = new RestfulResult();
        RestfulReturns restfulReturns = new RestfulReturns();
        String callback = request.getParameter("callback");

        String orderIdstr = request.getParameter("orderId");
        String orderItemSn = request.getParameter("orderItemSn");
        String orderItemId = request.getParameter("orderItemId");


        long orderId = 0;
        if (orderIdstr != null) {
            orderId = Long.parseLong(orderIdstr);
        } else {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_FAIL);
            restfulResult.setResult("orderId is null.");
            return returnRestfulResult(callback, restfulResult);
        }
        Returns returns = null;
        try {
            returns = returnsService.findByOrderIdAndItemId(orderId, orderItemSn);
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
        jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
            public boolean apply(Object obj, String name, Object value) {
                if (obj instanceof Returns && name.equals("handler")) {//Prodect init
                    return true;
                } else if (obj instanceof Returns && name.equals("order")) {//Prodect init
                    return true;
                } else if (obj instanceof ReturnsItem && name.equals("returns")) {//Prodect init
                    return true;
                } else {
                    return false;
                }
            }
        });
        restfulReturns.setReturns(returns);
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult("return success!");
        restfulResult.setReturnObj(restfulReturns);
        return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback, restfulResult, jsonConfig);
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

}