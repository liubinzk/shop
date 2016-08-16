package com.jqb.shop.restful.controller.pay;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import com.jqb.shop.Principal;
import com.jqb.shop.entity.*;
import com.jqb.shop.plugin.wxpay.api.CommonUtils;
import com.jqb.shop.plugin.wxpay.api.WxPayApi;
import com.jqb.shop.plugin.wxpay.api.WxPayData;
import com.jqb.shop.plugin.wxpay.lib.HttpService;
import com.jqb.shop.plugin.wxpay.lib.WxPayConfig;
import com.jqb.shop.plugin.wxpay.lib.WxPayException;
import com.jqb.shop.restful.entity.RestfulConstants;
import com.jqb.shop.restful.entity.RestfulResult;
import com.jqb.shop.service.*;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;


@Controller
@RequestMapping(value="rest")
public class UnifiedorderController {

	private static Logger Log = Logger.getLogger(UnifiedorderController.class);

	private static String payMethodName="微信支付";

	private static String mopenIdUrl = "https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=wx1834026d6576db3a&token=&lang=zh_CN";

	private static String openIdUrl = "https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=wx1834026d6576db3a&token=&lang=zh_CN";

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



	/***
	 * 统一下单
	 *
	 *  transaction_id
	 *            微信订单号（优先使用）
	 * @param out_trade_no
	 *            商户订单号
	 * @return 订单查询结果（xml格式）
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws WxPayException
	 * @throws NoSuchAlgorithmException
	 */
	@RequestMapping(value = "/pay_order")
	public @ResponseBody
	String payOrder( HttpServletRequest request, String out_trade_no)
			throws NoSuchAlgorithmException, WxPayException,
			ParserConfigurationException, SAXException, IOException {
		Log.info("payorder is processing...");
		String result = "";
		String callback = request.getParameter("callback");
		String memo = request.getParameter("memo");
		String receiverIdStr = request.getParameter("receiverId");
		long receiverId = 0;
		if (receiverIdStr != null && !"".equals(receiverIdStr)) {
			receiverId = Long.parseLong(receiverIdStr);
		}
		Order order = null;
		CustomerOrder customerOrder = null;
		int total_fee = 0;
		String body = "";
		String trade_type = "APP";
		WxPayData resultData = null;// 提交订单查询请求给API，接收返回数据
		try {
			if (out_trade_no != null) {
				order = orderService.findBySn(out_trade_no );
			}
			if (order != null ) {
				PaymentMethod paymentMethod = paymentMethodService.find(1L);
				order.setPaymentMethod(paymentMethod);
				order.setPaymentMethodName(this.payMethodName);
				if(memo != null && !memo.equals(order.getMemo())){
					order.setMemo(memo);
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
				orderService.update( order );
                if (!order.isExpired() && order.getPaymentStatus() == Order.PaymentStatus.unpaid  ) {
                    body = order.getOrderItems().get(0).getFullName();
					total_fee = com.jqb.shop.util.CommonUtils.formatWxFee( order.getAmount() );
                } else {
                    return returnResult(callback, "订单已支付");
                }
            } else {
				if (out_trade_no != null) {
					customerOrder = customerOrderService.findBySn( out_trade_no );
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

					customerOrderService.update( customerOrder );
					if (!customerOrder.isExpired() && customerOrder.getPaymentStatus() == CustomerOrder.PaymentStatus.unpaid  ) {
						body = customerOrder.getName();
						total_fee = com.jqb.shop.util.CommonUtils.formatWxFee(customerOrder.getAmount() );
					} else {
						return returnResult(callback, "订单已支付");
					}
				} else {
					return returnResult(callback, "订单失效");
				}
            }
//		for test set total_fee = 1分
//		out_trade_no += CommonUtils.timeStampMillis();
//			total_fee = 1;

			WxPayData data = new WxPayData();
			data.SetValue("body", body);//商品描述
			data.SetValue("out_trade_no", out_trade_no);//商户订单号
			data.SetValue("total_fee", total_fee);//总金额
			data.SetValue("trade_type", trade_type);//交易类型

			resultData = WxPayApi.unifiedorder(data, 0);


			Log.info("OrderQuery process complete, result : " + resultData.ToXml());
		} catch (Exception e) {
			RestfulResult restfulResult = new RestfulResult();
			restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
			restfulResult.setResult(e.toString());
			return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback, restfulResult);
		}

		WxPayData orderData  = new WxPayData();
		orderData.SetValue("appid", WxPayConfig.APPID);// appid
		orderData.SetValue("partnerid",resultData.GetValues().get("mch_id"));// partnerid
		orderData.SetValue("prepayid",resultData.GetValues().get("prepay_id"));// prepayid
		orderData.SetValue("package","Sign=WXPay");// 公众账号ID
		orderData.SetValue("noncestr", UUID.randomUUID().toString().replaceAll("-", "") );// noncestr
		orderData.SetValue("timestamp", CommonUtils.timeStampMillis());
		orderData.SetValue("sign", orderData.MakeSign("MD5"));// 签名
		result = orderData.ToJson();
		if (callback == null) {
			return result;
		} else {
			return callback + "('" + result + "')";
		}
	}
	@RequestMapping(value = "/wxpay_customer_result")
	public @ResponseBody
	String payCustomerOrderResult( HttpServletRequest request,  HttpServletResponse response) {
		Log.info("unifiedorder is processing...");
		RestfulResult restfulResult = new RestfulResult();
		String result = "";
		String callback = request.getParameter("callback");
		String customerOrderSn = request.getParameter("customerOrderSn");
		String errCode = request.getParameter("errCode");
		CustomerOrder order = null;
		try {
			if (customerOrderSn != null) {
				order = customerOrderService.findBySn(customerOrderSn );
			}
			if (errCode.equals("0")) {
				if(order.getPaymentStatus() != CustomerOrder.PaymentStatus.paid){

					if(order.getRealOrders() != null){
						for (Order realOrder : order.getRealOrders()) {
							realOrder.setPaymentStatus(Order.PaymentStatus.paid);
							realOrder.setPaymentMethodName(this.payMethodName);
							orderService.update(realOrder);
						}
					}
					order.setPaymentStatus(CustomerOrder.PaymentStatus.paid);
					order.setPaymentMethodName(this.payMethodName);
					customerOrderService.update(order);
				}
				restfulResult.setErrCode( RestfulConstants.RESTFUL_ERR_CODE_SUCCESS );
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
				}  else if(obj instanceof CustomerOrder && name.equals("realOrders")){//Member
					return true;
				} else{
					return false;
				}
			}
		});
		jsonConfig.setIgnoreDefaultExcludes(false);  //设置默认忽略

		jsonConfig.setExcludes(new String[]{"handler","hibernateLazyInitializer"});

		return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback, restfulResult, jsonConfig);

	}

	@RequestMapping(value = "/wxpay_result")
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
				order = orderService.findBySn( orderSn);
			}
			if (errCode.equals("0")) {
				if(order.getPaymentStatus() != Order.PaymentStatus.paid){
					order.setPaymentStatus(Order.PaymentStatus.paid);
					order.setPaymentMethodName(this.payMethodName);
					order.setAmountPaid( order.getAmount());
					orderService.update(order);
				}
				restfulResult.setErrCode( RestfulConstants.RESTFUL_ERR_CODE_SUCCESS );
			}
			if(order != null){
				order.initRestEntity();
				restfulResult.setReturnObj(order);
			}
		} catch (Exception e) {
			restfulResult = new RestfulResult();
			restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
			restfulResult.setResult(e.toString());
			return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback, restfulResult);
		}
		JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
		jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
			public boolean apply(Object obj, String name, Object value) {
				if (obj instanceof OrderItem && name.equals("order")) {
					return true;
				} else if (obj instanceof OrderItem && name.equals("product")) {//Prodect init
					return true;
				} else if (obj instanceof Order && name.equals("area")) {//Prodect init
					return true;
				} else if (obj instanceof Member || name.equals("orders")) {//Member
					return true;
				} else if (obj instanceof Returns && name.equals("order")) {//Member
					return true;
				} else if (obj instanceof Returns && name.equals("returnsItems")) {//Member
					return true;
				} else if (obj instanceof Shipping && name.equals("shippingItems")) {//Member
					return true;
				} else if (obj instanceof Shipping && name.equals("order")) {//Member
					return true;
				} else if (obj instanceof ShippingMethod && name.equals("defaultDeliveryCorp")) {//ShippingMethod
					return true;
				} else if (obj instanceof ShippingMethod && name.equals("paymentMethods")) {//ShippingMethod
					return true;
				} else if (obj instanceof ShippingMethod && name.equals("orders")) {//ShippingMethod
					return true;
				} else if(obj instanceof Order && name.equals("customerOrder")){//Member
					return true;
				}  else if(obj instanceof Order && name.equals("exchangeForOrder")){//Member
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

	@RequestMapping(value = "/wxpay_notify")
	public @ResponseBody
	String payOrderNotify( HttpServletRequest request,  HttpServletResponse response) {
		Log.info("wxpay order is begin:...");
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
				orderService.update(order);
			}
			Log.info("wxpay dorder notify :update success.");
		}
		restfulResult.setErrCode( RestfulConstants.RESTFUL_ERR_CODE_SUCCESS );
		return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback, restfulResult);
	}
	/***
	 * 订单查询完整业务流程逻辑
	 *
	 * @param transaction_id
	 *            微信订单号（优先使用）
	 * @param out_trade_no
	 *            商户订单号
	 * @return 订单查询结果（xml格式）
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws WxPayException
	 * @throws NoSuchAlgorithmException
	 */
	public  WxPayData getOrderList(String transaction_id, String out_trade_no)
			throws NoSuchAlgorithmException, WxPayException,
			ParserConfigurationException, SAXException, IOException {
		Log.info("OrderQuery is processing...");

		WxPayData data = new WxPayData();
		if (transaction_id != null && !transaction_id.isEmpty())// 如果微信订单号存在，则以微信订单号为准
		{
			data.SetValue("transaction_id", transaction_id);
		} else// 微信订单号不存在，才根据商户订单号去查单
		{
			data.SetValue("out_trade_no", out_trade_no);
		}

		WxPayData result = WxPayApi.OrderQuery(data, 0);// 提交订单查询请求给API，接收返回数据

		Log.info("OrderQuery process complete, result : " + result.ToXml());
		return result;
	}

	private String returnResult(String callback, String result) {
		if (callback == null) {
			return result;
		} else {
			return callback + "('" + result + "')";
		}
	}

	/***
	 * pay order result callback
	 *
	 *            商户订单号
	 * @return 订单查询结果（xml格式）
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws WxPayException
	 * @throws NoSuchAlgorithmException
	 */
	@RequestMapping(value = "/pay_result")
	public @ResponseBody
	String payOrderCallback( HttpServletRequest request,  HttpServletResponse response) {
		Log.info("unifiedorder is processing...");
		String result = "";
		String callback = request.getParameter("callback");
		// 将xml格式的结果转换为对象以返回
		WxPayData resultWxPayData = new WxPayData();

		try {
			resultWxPayData.FromXml(HttpService.unpackHttpResponse(request));
		} catch (WxPayException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		JSONObject jb = JSONObject.fromObject("{\"result\":\"" + resultWxPayData.GetValues().get("return_codes") +  "\"}");
		result = jb.toString();
		if (callback == null) {
			return result;
		} else {
			return callback + "('" + result + "')";
		}
	}

	/***
	 * pay order result callback
	 *
	 *            商户订单号
	 * @return 订单查询结果（xml格式）
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws WxPayException
	 * @throws NoSuchAlgorithmException
	 */
	@RequestMapping(value = "/pay_result_check")
	public @ResponseBody
	String payOrderCheck( HttpServletRequest request,  HttpServletResponse response) {
		Log.info("unifiedorder is processing...");
		WxPayData resultPayData = null;
		String callback = request.getParameter("callback");
		String out_trade_no = request.getParameter("201601061111");

		try {
			resultPayData = this.getOrderList(null,out_trade_no);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (WxPayException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject jb = JSONObject.fromObject("{\"result\":\"" + resultPayData.GetValues().get("return_codes") +  "\"}");
		String result = jb.toString();
		if (callback == null) {
			return result;
		} else {
			return callback + "('" + result + "')";
		}
	}

	public static void main(String[] args) {
		String out_trade_no = "201601061111";
		UnifiedorderController unifiedorderController = new UnifiedorderController();
		WxPayData result = null;
		try {
			result = unifiedorderController.getOrderList(null,out_trade_no);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (WxPayException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

//		UnifiedorderService.

//
		//UnifiedorderController.getOpenId();

//		WxPayData data = new WxPayData();
//		String body = "Ipad mini  16G  白色";
//		String out_trade_no = "20150806125346";
//		String trade_type = "APP";
//		int total_fee = 1;
//		data.SetValue("body", body);//商品描述
//		data.SetValue("out_trade_no", out_trade_no);//商户订单号
//		data.SetValue("total_fee", total_fee);//总金额
//		data.SetValue("trade_type", trade_type);//交易类型
//		data.SetValue("spbill_create_ip", WxPayConfig.IP);// 终端ip
//		data.SetValue("appid", WxPayConfig.APPID);// 公众账号ID
//		data.SetValue("mch_id", WxPayConfig.MCHID);// 商户号
//		data.SetValue("nonce_str",
//				"muqxwyu519br5nv832p0");// 随机字符串
//		data.SetValue("notify_url", WxPayConfig.NOTIFY_URL);// NOTIFY_URL
//		try {
//			data.SetValue("sign", data.MakeSign("MD5"));// 签名
//			System.out.println("sign:" + data.MakeSign("MD5"));
//		} catch (WxPayException e) {
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		try {
//			String xml = data.ToXml();
//		} catch (WxPayException e) {
//			e.printStackTrace();
//		}

	}

	public static String getOpenId(){
		long start = System.currentTimeMillis();// 请求开始时间
		Log.debug("openId request : ");
		String response = "";
		try {
			response = HttpService.Get(mopenIdUrl);
		} catch (WxPayException e1) {
			e1.printStackTrace();
		}// 调用HTTP通信接口以提交数据到API
		Log.debug("openId response : " + response);

		long end = System.currentTimeMillis();
		int timeCost = (int) ((end - start));// 获得接口耗时

		// 将xml格式的结果转换为对象以返回
		WxPayData result = new WxPayData();
		try {
			result.FromXml(response);
		} catch (WxPayException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
