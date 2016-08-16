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
import com.jqb.shop.service.ProductNotifyService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller - 到货通知
 * 
 * @author JQB Team
 * @version 3.0
 */
@Controller("ProductNotifyntroller")
@RequestMapping("/admin/product_notify")
public class ProductNotifyController extends BaseController {

	@Resource(name = "productNotifyServiceImpl")
	private ProductNotifyService productNotifyService;

	/**
	 * 发送到货通知
	 */
	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public @ResponseBody
	Message send(Long[] ids) {
		int count = productNotifyService.send(ids);
		return Message.success("admin.productNotify.sentSuccess", count);
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Boolean isMarketable, Boolean isOutOfStock, Boolean hasSent, Pageable pageable, ModelMap model) {
		model.addAttribute("isMarketable", isMarketable);
		model.addAttribute("isOutOfStock", isOutOfStock);
		model.addAttribute("hasSent", hasSent);
		model.addAttribute("page", productNotifyService.findPage(null, isMarketable, isOutOfStock, hasSent, pageable));
		return "/admin/product_notify/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody
	Message delete(Long[] ids) {
		productNotifyService.delete(ids);
		return SUCCESS_MESSAGE;
	}

}
