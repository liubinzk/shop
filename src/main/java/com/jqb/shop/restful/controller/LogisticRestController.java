package com.jqb.shop.restful.controller;

import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.Principal;
import com.jqb.shop.entity.*;
import com.jqb.shop.restful.entity.RestfulConstants;
import com.jqb.shop.restful.entity.RestfulResult;
import com.jqb.shop.service.*;
import com.jqb.shop.util.CommonUtils;
import com.jqb.shop.util.SpringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Controller
@RequestMapping(value="rest")
public class LogisticRestController {

    /**
     * Logging for this instance
     */
    private org.apache.commons.logging.Log log = LogFactory.getLog(LogisticRestController.class);

    /** Used to access properties*/
    private PropertyUtilsBean propertyUtilsBean;

    /** Used to perform conversions between object types when setting properties */
    private ConvertUtilsBean convertUtilsBean;

    @Resource(name = "memberServiceImpl")
    private MemberService memberService;


    @Resource(name = "paymentMethodServiceImpl")
    private PaymentMethodService paymentMethodService;

    @Resource(name = "shippingMethodServiceImpl")
    private ShippingMethodService shippingMethodService;

    @Resource(name = "areaServiceImpl")
    private AreaService areaService;
    @Resource(name = "receiverServiceImpl")
    private ReceiverService receiverService;


    @RequestMapping("/receiver")
    public @ResponseBody  String getReceiver(Model model, HttpServletRequest request) {
        RestfulResult restfulResult = new RestfulResult();
        String callback = request.getParameter("callback");
        String receiverIdStr = request.getParameter("receiverId");
        String consignee = request.getParameter("consignee");

        long receiverId = 0;
        if (receiverIdStr != null && !"".equals(receiverIdStr)) {
            receiverId = Long.parseLong(receiverIdStr);
        }
        Receiver receiver = null;
        JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
        try {
            if(receiverId != 0){
                receiver = receiverService.find( receiverId );
            } else if(consignee != null && !"".equalsIgnoreCase(consignee) ){
               List<Receiver> receiverList = receiverService.findList(consignee);
                if(receiverList != null && receiverList.size() > 0){
                    receiver = receiverList.get(0);
                }
            } else {
                //权限校验
                Member member = memberService.getCurrent();
                if (member.getReceivers() != null && member.getReceivers().size() > 0) {
                    for (Receiver receiverElem : member.getReceivers()) {
                        if (receiverElem.getIsDefault()) {
                            receiver = receiverElem;
                            break;
                        }
                    }
                    if (receiver == null) {
                        receiver = (Receiver)member.getReceivers().toArray()[0];
                    }
                }
            }

            jsonConfig.setIgnoreDefaultExcludes(false);  //设置默认忽略
            jsonConfig.setExcludes(new String[]{"area", "member"});
        } catch (Exception e){
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setReturnObj(receiver);
        return CommonUtils.returnRestfulResult(callback, restfulResult,jsonConfig);
    }

    @RequestMapping("/receiver_detail")
    public @ResponseBody  String getReceiverInfo(Model model, HttpServletRequest request) {
        AdPosition adPosition = null;
        String callback = request.getParameter("callback");
        String receiverIdstr = request.getParameter("receiverId");
        long receiverId = 0;
        if (receiverIdstr != null && !"".equals(receiverIdstr)) {
            receiverId = Long.parseLong(receiverIdstr);
        }
        JSONArray jb = null;
        String result = null;
        try {
            Receiver receiver =  receiverService.find(receiverId);
            //
            JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
            jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
                public boolean apply(Object obj, String name, Object value) {
                    if(obj instanceof Product && name.equals("handler")  ){//Prodect init
                        return true;
                    } else  if(obj instanceof Area && name.equals("parent") ){//Prodect init
                        return true;
                    } else  if(obj instanceof Area && name.equals("children") ){//Prodect init
                        return true;
                    } else  if(obj instanceof Area && name.equals("members")){//Prodect init
                        return true;
                    } else  if(obj instanceof Area && name.equals("receivers")){//Prodect init
                        return true;
                    } else  if(obj instanceof Area && name.equals("orders")){//Prodect init  deliveryCenters
                        return true;
                    } else  if(obj instanceof Area && name.equals("deliveryCenters")){//Prodect init
                        return true;
                    }else{
                        return false;
                    }

                }
            });
            jsonConfig.setIgnoreDefaultExcludes(false);  //设置默认忽略
            jsonConfig.setExcludes(new String[]{"handler", "hibernateLazyInitializer","member"});

            jb = JSONArray.fromObject(receiver, jsonConfig);
            result = jb.toString();
        } catch (Exception e) {
            RestfulResult restfulResult = new RestfulResult();
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }

        if (callback == null) {
            return result;
        } else {
            return callback + "('" +  result + "')";
        }
    }

    /***
     * 支付方式
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/pay_method")
    public @ResponseBody  String getPayMethod(Model model, HttpServletRequest request) {
        RestfulResult restfulResult = new RestfulResult();
        String callback = request.getParameter("callback");

        JSONArray jb = null;
        String result = "";
        JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
        List<PaymentMethod> paymentMethods = null;
        try {
            paymentMethods = paymentMethodService.findAll();
            for (PaymentMethod paymentMethod : paymentMethods) {
                paymentMethod.setContent("");
            }
            //
            jsonConfig.setIgnoreDefaultExcludes(false);  //设置默认忽略
            jsonConfig.setExcludes(new String[]{"shippingMethods", "orders"});

        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setReturnObj(paymentMethods);

        return CommonUtils.returnRestfulResult(callback, restfulResult,jsonConfig );
    }

    /***
     * 配送方式
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/delivery_method")
    public @ResponseBody  String getDeliveryMethod(Model model, HttpServletRequest request) {
        RestfulResult restfulResult = new RestfulResult();
        String callback = request.getParameter("callback");

        JSONArray jb = null;
        List<ShippingMethod> shippingMethods = null;
        try {
            shippingMethods = shippingMethodService.findAll();
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
        jsonConfig.setIgnoreDefaultExcludes(false);  //设置默认忽略
        jsonConfig.setExcludes(new String[]{"paymentMethods", "orders", "defaultDeliveryCorp"});
        restfulResult.setReturnObj(shippingMethods);
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        return CommonUtils.returnRestfulResult(callback,restfulResult,jsonConfig);
    }


    @RequestMapping("/save_receiver")
    public @ResponseBody  String saveReceiver(Model model, HttpServletRequest request) {
        String result = "";
        String callback = request.getParameter("callback");
        String userName = request.getParameter("userName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String areaIdStr = request.getParameter("areaId");
        String zipCode = request.getParameter("zipCode");
        long areaId = 0;
        if (areaIdStr != null && !"".equals(areaIdStr)) {
            areaId = Long.parseLong(areaIdStr);
        } else {
            result = "请选择地区";
            return returnResult(callback, result);
        }
        try {
            Receiver   receiver = new Receiver();
            Area area = areaService.find(areaId);
            receiver.setArea( area );
            Member member = memberService.getCurrent();
            if(member == null){
                RestfulResult restfulResult = new RestfulResult();
                restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_NO_MEMBER);
                return returnRestfulResult(callback, restfulResult);
            }
            if (Receiver.MAX_RECEIVER_COUNT != null && member.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT+12) {
                RestfulResult restfulResult = new RestfulResult();
                restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_FAIL);
                restfulResult.setResult("地址数量超出限制。");
                return CommonUtils.returnRestfulResult(callback, restfulResult);
            }
            receiver.setIsDefault(true);
            receiver.setMember(member);
            receiver.setCreateDate(new Date());
            receiver.setPhone(phone);
            receiver.setConsignee(userName);
            receiver.setAddress( address );
            receiver.setZipCode(zipCode);
            receiverService.save(receiver);
        } catch (Exception e) {
            RestfulResult restfulResult = new RestfulResult();
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        result = SpringUtils.getMessage("shop.message.success");

//        JSONArray jb = JSONArray.fromObject ("");
//        String result = jb.toString();

        RestfulResult restfulResult = new RestfulResult();
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult("save address success.");
        return CommonUtils.returnRestfulResult(callback, restfulResult);
    }

    @RequestMapping("/update_receiver")
    public @ResponseBody  String updateReceiver(Model model, HttpServletRequest request) {
        String result = "";
        String callback = request.getParameter("callback");
        String receiverIdstr = request.getParameter("receiverId");
        String userName = request.getParameter("userName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String areaIdStr = request.getParameter("areaId");
        String zipCode = request.getParameter("zipCode");

        long receiverId = 0;
        if (receiverIdstr != null && !"".equals(receiverIdstr)) {
            receiverId = Long.parseLong(receiverIdstr);
        }
        try {
            Receiver receiver =  receiverService.find(receiverId);

            long areaId = 0;
            if (areaIdStr != null && !"".equals(areaIdStr)) {
                areaId = Long.parseLong(areaIdStr);
            } else {
                result = "请选择地区";
                return returnResult(callback, result);
            }
            Area area = areaService.find(areaId);
            receiver.setArea( area );
            Member member = memberService.getCurrent();
            if (Receiver.MAX_RECEIVER_COUNT != null && member.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT+12) {
                result = "地址数量超出限制。";
                return returnResult(callback, result);
            }
            receiver.setIsDefault(true);
            receiver.setMember(member);
            receiver.setCreateDate(new Date());
            receiver.setPhone(phone);
            receiver.setConsignee(userName);
            receiver.setAddress( address );
            receiver.setZipCode(zipCode);
            receiverService.save(receiver);
        } catch (NumberFormatException e) {
            RestfulResult restfulResult = new RestfulResult();
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        result = SpringUtils.getMessage("shop.message.success");

//        JSONArray jb = JSONArray.fromObject ("");
//        String result = jb.toString();
        RestfulResult restfulResult = new RestfulResult();
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult("update address success.");
        return CommonUtils.returnRestfulResult(callback, restfulResult);
    }

    @RequestMapping("/set_default_receiver")
    public @ResponseBody  String selectReceiver(Model model, HttpServletRequest request) {
        String result = "";
        String callback = request.getParameter("callback");
        String receiverIdstr = request.getParameter("receiverId");

        long receiverId = 0;
        if (receiverIdstr != null && !"".equals(receiverIdstr)) {
            receiverId = Long.parseLong(receiverIdstr);
        }
        try {
            Receiver receiver =  receiverService.find(receiverId);

            Member member = memberService.getCurrent();
            if (Receiver.MAX_RECEIVER_COUNT != null && member.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT+12) {
                result = "地址数量超出限制。";
                return returnResult(callback, result);
            }
            receiver.setIsDefault(true);
            receiver.setMember(member);
            receiverService.save(receiver);
        } catch (Exception e) {
            RestfulResult restfulResult = new RestfulResult();
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        result = SpringUtils.getMessage("shop.message.success");

//        JSONArray jb = JSONArray.fromObject ("");
//        String result = jb.toString();

        RestfulResult restfulResult = new RestfulResult();
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult("select address success.");
        return CommonUtils.returnRestfulResult(callback, restfulResult);
    }
    
    
   @RequestMapping("/delete_receiver")
    public @ResponseBody  String deleteReceiver(Model model, HttpServletRequest request) {
        String callback = request.getParameter("callback");
        String receiverIdstr = request.getParameter("receiverId");
        long receiverId = 0;
        if (receiverIdstr != null && !"".equals(receiverIdstr)) {
            receiverId = Long.parseLong(receiverIdstr);
        }
        try {
            Receiver  receiver =  receiverService.find(receiverId);
            receiverService.delete(receiver);
        } catch (Exception e) {
            RestfulResult restfulResult = new RestfulResult();
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        RestfulResult restfulResult = new RestfulResult();
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult("delete address success.");
        return CommonUtils.returnRestfulResult(callback, restfulResult);
    }

    @RequestMapping("/receiver_list")
    public @ResponseBody  String getReceiverList(Model model, HttpServletRequest request) {
        AdPosition adPosition = null;
        String callback = request.getParameter("callback");

        JSONArray jb = null;
        RestfulResult restfulResult = new RestfulResult();
        JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
        try {
            Member member = memberService.getCurrent();
            if(member == null){
                restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_NO_MEMBER);
                return CommonUtils.returnRestfulResult(callback, restfulResult);
            }
            Pageable pageable = new Pageable(1, 20);
            Page<Receiver> receverList =  receiverService.findPage(member, pageable);
            //
            jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
                public boolean apply(Object obj, String name, Object value) {
                    if(obj instanceof Receiver && name.equals("member")){
                        return true;
                    }
                    else  if(obj instanceof Area && name.equals("parent")){//Prodect init
                        return true;
                    } else  if(obj instanceof Area && name.equals("children")){//Prodect init
                        return true;
                    } else  if(obj instanceof Area && name.equals("members")){//Prodect init
                        return true;
                    } else  if(obj instanceof Area && name.equals("receivers")){//Prodect init
                        return true;
                    } else  if(obj instanceof Area && name.equals("orders")){//Prodect init
                        return true;
                    } else  if(obj instanceof Area && name.equals("deliveryCenters")){//Prodect init
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            jsonConfig.setIgnoreDefaultExcludes(false);  //设置默认忽略
            jsonConfig.setExcludes(new String[]{"handler", "hibernateLazyInitializer"});
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
            restfulResult.setReturnObj(receverList);
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return CommonUtils.returnRestfulResult(callback, restfulResult);
        }

       return CommonUtils.returnRestfulResult(callback, restfulResult, jsonConfig);
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