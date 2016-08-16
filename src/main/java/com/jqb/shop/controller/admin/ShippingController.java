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

import com.jqb.shop.Filter;
import com.jqb.shop.Message;
import com.jqb.shop.Pageable;
import com.jqb.shop.Filter.Operator;
import com.jqb.shop.entity.Admin;
import com.jqb.shop.service.AdminService;
import com.jqb.shop.service.ShippingService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller - 发货单
 * 
 * @author JQB Team
 * @version 3.0
 */
@Controller("adminShippingController")
@RequestMapping("/admin/shipping")
public class ShippingController extends BaseController {

	@Resource(name = "shippingServiceImpl")
	private ShippingService shippingService;
	@Resource(name = "adminServiceImpl")
	private AdminService adminService;
	/**
	 * 查看
	 */
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String view(Long id, ModelMap model) {
		model.addAttribute("shipping", shippingService.find(id));
		return "/admin/shipping/view";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		pageable.getFilters().add(this.filterCurrentAdminByCommercial());
		model.addAttribute("page", shippingService.findPage(pageable));
		return "/admin/shipping/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody
	Message delete(Long[] ids) {
		shippingService.delete(ids);
		return SUCCESS_MESSAGE;
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
