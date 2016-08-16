/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.controller.admin;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.jqb.shop.Filter;
import com.jqb.shop.Message;
import com.jqb.shop.Pageable;
import com.jqb.shop.Filter.Operator;
import com.jqb.shop.entity.*;
import com.jqb.shop.restful.entity.RestfulConstants;
import com.jqb.shop.restful.entity.RestfulResult;
import com.jqb.shop.service.*;
import com.jqb.shop.util.CommonUtils;
import com.jqb.shop.util.SpringUtils;

import javafx.application.Application;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.Hibernate;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

/**
 * Controller - 退货单
 * 
 * @author JQB Team
 * @version 3.0
 */
@Controller("adminReturnsController")
@RequestMapping("/admin/returns")
public class ReturnsController extends BaseController {

	@Resource(name = "returnsServiceImpl")
	private ReturnsService returnsService;
	@Resource(name = "adminServiceImpl")
	private AdminService adminService;
	@Resource(name = "productServiceImpl")
	private ProductService productService;
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	@Resource(name = "receiverServiceImpl")
	private ReceiverService receiverService;
	@Resource(name = "paymentMethodServiceImpl")
	private PaymentMethodService paymentMethodService;
	@Resource(name = "shippingMethodServiceImpl")
	private ShippingMethodService shippingMethodService;
	@Resource(name = "orderServiceImpl")
	private OrderService orderService;
	@Resource(name = "couponCodeServiceImpl")
	private CouponCodeService couponCodeService;
	@Resource(name = "snServiceImpl")
	private SnService snService;

	/**
	 * 查看
	 */
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String view(Long id, ModelMap model) {
		model.addAttribute("returns", returnsService.find(id));
		return "/admin/returns/view";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		pageable.getFilters().add(this.filterCurrentAdminByCommercial());
		model.addAttribute("page", returnsService.findPage(pageable));
		return "/admin/returns/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody
	Message delete(Long[] ids) {
		returnsService.delete(ids);
		return SUCCESS_MESSAGE;
	}


	/**
	 * 审核通过，确认
	 */
	@RequestMapping(value = "/confirm", method = RequestMethod.POST)
	public String confirm(Long id, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		Returns returns = returnsService.find(id);
		Admin admin = adminService.getCurrent();
//		Admin currentAdmin = adminService.getCurrent();

		if(admin != null ){
			if (admin.getReceivers() != null && admin.getReceivers().size() > 0) {
				boolean hasAddress = false;
				for (AdminReceiver receiverElem : admin.getReceivers()) {
					if (receiverElem.getIsDefault()) {
						hasAddress = true;
						break;
					}
				}
				if(!hasAddress){
					addFlashMessage(redirectAttributes, Message.error("admin.receiver.address.noDefault"));
					return "redirect:view.jhtml?id=" + id;
				}
			} else {
				String base = (String)request.getSession().getAttribute("base");
				Message msg =  Message.error("admin.receiver.no.address");
				msg.setContent(msg.getContent() + base + "/");
				addFlashMessage(redirectAttributes, msg);
				return "redirect:view.jhtml?id=" + id;
			}
		} else {
			addFlashMessage(redirectAttributes, Message.error("admin.not.exist"));
			return "redirect:view.jhtml?id=" + id;
		}

		if(returns!=null){
			if (admin == null) {
				String base = (String)request.getSession().getAttribute("base");
				Message msg =  Message.error("admin.receiver.no.address");
				msg.setContent(msg.getContent() + base + "/");
				addFlashMessage(redirectAttributes, msg);
				return "redirect:view.jhtml?id=" + id;
			}
			returns.setOperator(admin.getUsername());
			returns.setReturnsStatus(Returns.ReturnsStatus.confirmed);
			returnsService.update(returns);
			addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		} else {
			addFlashMessage(redirectAttributes, Message.warn("admin.common.invalid"));
		}
		return "redirect:view.jhtml?id=" + id;
	}

	/**
	 * 审核不通过，拒绝
	 */
	@RequestMapping(value = "/deny", method = RequestMethod.POST)
	public String deny(Long id, RedirectAttributes redirectAttributes,  HttpServletRequest request) {
		Returns returns = returnsService.find(id);
		Admin admin = adminService.getCurrent();
//		Admin currentAdmin = adminService.getCurrent();

		if(admin != null ){
			if (admin.getReceivers() != null && admin.getReceivers().size() > 0) {
				boolean hasAddress = false;
				for (AdminReceiver receiverElem : admin.getReceivers()) {
					if (receiverElem.getIsDefault()) {
						hasAddress = true;
						break;
					}
				}
				if(!hasAddress){
					addFlashMessage(redirectAttributes, Message.error("admin.receiver.address.noDefault"));
					return "redirect:view.jhtml?id=" + id;
				}
			} else {
				String base = (String)request.getSession().getAttribute("base");
				Message msg =  Message.error("admin.receiver.no.address");
				msg.setContent(msg.getContent() + base + "/");
				addFlashMessage(redirectAttributes, msg);
				return "redirect:view.jhtml?id=" + id;
			}
		} else {
			addFlashMessage(redirectAttributes, Message.error("admin.not.exist"));
			return "redirect:view.jhtml?id=" + id;
		}

		if(returns!=null){
			if (admin == null) {
				String base = (String)request.getSession().getAttribute("base");
				Message msg =  Message.error("admin.receiver.no.address");
				msg.setContent(msg.getContent() + base + "/");
				addFlashMessage(redirectAttributes, msg);
				return "redirect:view.jhtml?id=" + id;
			}
			returns.setOperator(admin.getUsername());
			returns.setReturnsStatus(Returns.ReturnsStatus.denied);
			returnsService.update(returns);
			addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		} else {
			addFlashMessage(redirectAttributes, Message.warn("admin.common.invalid"));
		}
		return "redirect:view.jhtml?id=" + id;
	}
	@RequestMapping(value = "/exchange_order", method = RequestMethod.GET)
	public String addImmediateCart(String productSn, Integer quantity,long returnsId,RedirectAttributes redirectAttributes, HttpServletRequest request) {
		Returns returns = returnsService.find(returnsId);

		if (returns.getExchangeOrder() != null) {

			addFlashMessage(redirectAttributes, Message.warn("The order was returned!"));
			return "redirect:view.jhtml?id=" + returnsId;
		}

		Cart cart = null;
		Member member = null;
		Order order = null;
		Product returnsProduct = productService.findBySn(productSn);
		if (returnsProduct == null) {
			addFlashMessage(redirectAttributes, Message.warn("shop.cart.productNotExsit"));
			return "redirect:view.jhtml?id=" + returnsId;
		}
		if (!returnsProduct.getIsMarketable()) {
			addFlashMessage(redirectAttributes, Message.warn("shop.cart.productNotMarketable"));
			return "redirect:view.jhtml?id=" + returnsId;
		}
		if (returnsProduct.getIsGift()) {
			addFlashMessage(redirectAttributes, Message.warn("shop.cart.notForSale"));
			return "redirect:view.jhtml?id=" + returnsId;
		}

		member = returns.getOrder().getMember();

		if (cart == null) {
			cart = new Cart();
			cart.setKey(UUID.randomUUID().toString() + DigestUtils.md5Hex(RandomStringUtils.randomAlphabetic(30)));
			cart.setMember(member);
		}

		if (Cart.MAX_PRODUCT_COUNT != null && cart.getCartItems().size() >= Cart.MAX_PRODUCT_COUNT) {
			addFlashMessage(redirectAttributes, Message.warn("shop.cart.addCountNotAllowed", Cart.MAX_PRODUCT_COUNT));
			return "redirect:view.jhtml?id=" + returnsId;
		}


		if (CartItem.MAX_QUANTITY != null && quantity > CartItem.MAX_QUANTITY) {
			addFlashMessage(redirectAttributes, Message.warn("shop.cart.maxCartItemQuantity", CartItem.MAX_QUANTITY));
			return "redirect:view.jhtml?id=" + returnsId;
		}
		if (returnsProduct.getStock() != null && quantity > returnsProduct.getAvailableStock()) {
			addFlashMessage(redirectAttributes, Message.warn("shop.cart.productLowStock"));
			return "redirect:view.jhtml?id=" + returnsId;
		}
		CartItem returnsCartItem = new CartItem();
		returnsCartItem.setQuantity(quantity);
		returnsCartItem.setProduct(returnsProduct);
		returnsCartItem.setCart(cart);
		cart.getCartItems().add(returnsCartItem);
		if (cart == null || cart.isEmpty()) {
			addFlashMessage(redirectAttributes, Message.warn("Sorry!Please select product."));
			return "redirect:view.jhtml?id=" + returnsId;
		}

		if (!isValid(cart)) {
			addFlashMessage(redirectAttributes, Message.warn("Sorry! Please selcet products"));
			return "redirect:view.jhtml?id=" + returnsId;
		}


		if (cart.getIsLowStock()) {
			addFlashMessage(redirectAttributes, Message.warn("shop.order.cartLowStock"));
			return "redirect:view.jhtml?id=" + returnsId;
		}
		order = new Order();
		String[] ignoreProperties={"id","sn","orderItems"};
		Order oldOrder = returns.getOrder();
		CommonUtils.copyProperties(oldOrder, order,ignoreProperties);
		order.setOrderStatus(Order.OrderStatus.unconfirmed);
		order.setShippingStatus(Order.ShippingStatus.unshipped);
		List<OrderItem> orderItems = order.getOrderItems();
		for (CartItem cartItem : cart.getCartItems()) {
			if (cartItem != null && cartItem.getProduct() != null) {
				Product product = cartItem.getProduct();
				OrderItem orderItem = new OrderItem();
				orderItem.setSn(product.getSn());
				orderItem.setName(product.getName());
				orderItem.setFullName(product.getFullName());
				orderItem.setPrice(cartItem.getPrice());
				orderItem.setWeight(product.getWeight());
				orderItem.setThumbnail(product.getThumbnail());
				orderItem.setIsGift(false);
				orderItem.setQuantity(cartItem.getQuantity());
				orderItem.setShippedQuantity(0);
				orderItem.setReturnQuantity(0);
				orderItem.setProduct(product);
				orderItem.setOrder(order);
				//commercial 结算
				if(product.getCost()==null){
					orderItem.setCost(cartItem.getPrice());
				} else {
					orderItem.setCost(product.getCost());
				}
				orderItems.add(orderItem);
			}
		}
		Admin admin = adminService.getCurrent();
		order.setExchangeForOrder(oldOrder);
		orderService.createReturnsOrder(cart, order, admin);

		returns.setExchangeOrder(order);
		returnsService.update(returns);

		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:view.jhtml?id=" + returnsId;
	}
	private Filter filterCurrentAdminByCommercial(){
		Admin admin=adminService.getCurrent();
		 Filter filter=new Filter();
		if(admin.getCommercial()!= null){//商户null代表超级管理员
			 filter=new Filter("order.commercial",Operator.eq,admin.getCommercial().getId());
		}
		return filter;
	}
}
