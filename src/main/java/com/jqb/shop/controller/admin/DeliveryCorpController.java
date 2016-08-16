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

import com.jqb.shop.Message;
import com.jqb.shop.Pageable;
import com.jqb.shop.entity.DeliveryCorp;
import com.jqb.shop.service.DeliveryCorpService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller - 物流公司
 * 
 * @author JQB Team
 * @version 3.0
 */
@Controller("adminDeliveryCorpController")
@RequestMapping("/admin/delivery_corp")
public class DeliveryCorpController extends BaseController {

	@Resource(name = "deliveryCorpServiceImpl")
	private DeliveryCorpService deliveryCorpService;

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add() {
		return "/admin/delivery_corp/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(DeliveryCorp deliveryCorp, RedirectAttributes redirectAttributes) {
		if (!isValid(deliveryCorp)) {
			return ERROR_VIEW;
		}
		deliveryCorp.setShippingMethods(null);
		deliveryCorpService.save(deliveryCorp);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		model.addAttribute("deliveryCorp", deliveryCorpService.find(id));
		return "/admin/delivery_corp/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(DeliveryCorp deliveryCorp, RedirectAttributes redirectAttributes) {
		if (!isValid(deliveryCorp)) {
			return ERROR_VIEW;
		}
		deliveryCorpService.update(deliveryCorp, "shippingMethods");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", deliveryCorpService.findPage(pageable));
		return "/admin/delivery_corp/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody
	Message delete(Long[] ids) {
		deliveryCorpService.delete(ids);
		return SUCCESS_MESSAGE;
	}

}
