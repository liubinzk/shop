/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.controller.admin;

import java.util.HashSet;

import javax.annotation.Resource;

import com.jqb.shop.Message;
import com.jqb.shop.Pageable;
import com.jqb.shop.entity.PaymentMethod;
import com.jqb.shop.entity.PaymentMethod.Method;
import com.jqb.shop.entity.ShippingMethod;
import com.jqb.shop.service.PaymentMethodService;
import com.jqb.shop.service.ShippingMethodService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller - 支付方式
 * 
 * @author JQB Team
 * @version 3.0
 */
@Controller("adminPaymentMethodController")
@RequestMapping("/admin/payment_method")
public class PaymentMethodController extends BaseController {

	@Resource(name = "paymentMethodServiceImpl")
	private PaymentMethodService paymentMethodService;
	@Resource(name = "shippingMethodServiceImpl")
	private ShippingMethodService shippingMethodService;

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap model) {
		model.addAttribute("methods", Method.values());
		model.addAttribute("shippingMethods", shippingMethodService.findAll());
		return "/admin/payment_method/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(PaymentMethod paymentMethod, Long[] shippingMethodIds, RedirectAttributes redirectAttributes) {
		paymentMethod.setShippingMethods(new HashSet<ShippingMethod>(shippingMethodService.findList(shippingMethodIds)));
		if (!isValid(paymentMethod)) {
			return ERROR_VIEW;
		}
		paymentMethod.setOrders(null);
		paymentMethodService.save(paymentMethod);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		model.addAttribute("methods", Method.values());
		model.addAttribute("shippingMethods", shippingMethodService.findAll());
		model.addAttribute("paymentMethod", paymentMethodService.find(id));
		return "/admin/payment_method/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(PaymentMethod paymentMethod, Long[] shippingMethodIds, RedirectAttributes redirectAttributes) {
		paymentMethod.setShippingMethods(new HashSet<ShippingMethod>(shippingMethodService.findList(shippingMethodIds)));
		if (!isValid(paymentMethod)) {
			return ERROR_VIEW;
		}
		paymentMethodService.update(paymentMethod, "orders");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", paymentMethodService.findPage(pageable));
		return "/admin/payment_method/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody
	Message delete(Long[] ids) {
		if (ids.length >= paymentMethodService.count()) {
			return Message.error("admin.common.deleteAllNotAllowed");
		}
		paymentMethodService.delete(ids);
		return SUCCESS_MESSAGE;
	}

}
