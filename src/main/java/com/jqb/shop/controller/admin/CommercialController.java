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

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jqb.shop.Message;
import com.jqb.shop.Pageable;
import com.jqb.shop.controller.shop.BaseController;
import com.jqb.shop.entity.Admin;
import com.jqb.shop.entity.Commercial;
import com.jqb.shop.service.AdminService;
import com.jqb.shop.service.AreaService;
import com.jqb.shop.service.CommercialService;

/**
 * Controller - 商户
 * 
 * @author JQB Team
 * @version 3.0
 */
@Controller("shopCommercialController")
@RequestMapping("/admin/commercial")
public class CommercialController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 50;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;
	@Resource(name = "areaServiceImpl")
	private AreaService areaService;
	@Resource(name = "commercialServiceImpl")
	private CommercialService commercialService;

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		Admin admin = adminService.getCurrent();
//		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		model.addAttribute("page", commercialService.findPage( pageable));
		return "admin/commercial/list";
	}

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add() {
		return "/admin/commercial/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(Commercial commercial, RedirectAttributes redirectAttributes) {
		if (!isValid(commercial)) {
			return ERROR_VIEW;
		}
		commercialService.save(commercial);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		model.addAttribute("commercial", commercialService.find(id));
		return "/admin/commercial/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(Commercial commercial, RedirectAttributes redirectAttributes) {
		if (!isValid(commercial)) {
			return ERROR_VIEW;
		}
		commercialService.update(commercial);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}


	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody
	Message delete(Long[] ids) {
		try {
			for(Long id: ids){
				commercialService.deleteAndCheck(id);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return Message.error(e.getMessage());
		}
		return SUCCESS_MESSAGE;
	}
}
