/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.controller.admin;

import com.jqb.shop.Message;
import com.jqb.shop.Pageable;
import com.jqb.shop.controller.shop.BaseController;
import com.jqb.shop.entity.Admin;
import com.jqb.shop.entity.AdminReceiver;
import com.jqb.shop.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;

/**
 * Controller - 管理员 - 收货地址
 * 
 * @author JQB Team
 * @version 3.0
 */
@Controller("adminReceiverController")
@RequestMapping("/admin/admin/receiver")
public class AdminReceiverController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;
	@Resource(name = "areaServiceImpl")
	private AreaService areaService;
	@Resource(name = "adminReceiverServiceImpl")
	private AdminReceiverService adminReceiverService;

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Integer pageNumber, ModelMap model) {
		Admin admin = adminService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		model.addAttribute("page", adminReceiverService.findPage(admin, pageable));
		return "admin/admin/receiver/list";
	}

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(RedirectAttributes redirectAttributes) {
		Admin admin = adminService.getCurrent();
		if (AdminReceiver.MAX_RECEIVER_COUNT != null && admin.getReceivers().size() >= AdminReceiver.MAX_RECEIVER_COUNT) {
			addFlashMessage(redirectAttributes, Message.warn("admin.member.receiver.addCountNotAllowed", AdminReceiver.MAX_RECEIVER_COUNT));
			return "redirect:list.jhtml";
		}
		return "admin/admin/receiver/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(AdminReceiver receiver, Long areaId, RedirectAttributes redirectAttributes) {
		receiver.setArea(areaService.find(areaId));
		if (!isValid(receiver)) {
			return ERROR_VIEW;
		}
		Admin admin = adminService.getCurrent();
		if (AdminReceiver.MAX_RECEIVER_COUNT != null && admin.getReceivers().size() >= AdminReceiver.MAX_RECEIVER_COUNT) {
			return ERROR_VIEW;
		}
		receiver.setAdmin(admin);
		adminReceiverService.save(receiver);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model, RedirectAttributes redirectAttributes) {
		AdminReceiver receiver = adminReceiverService.find(id);
		if (receiver == null) {
			return ERROR_VIEW;
		}
		Admin admin = adminService.getCurrent();
		if (!admin.equals(receiver.getAdmin())) {
			return ERROR_VIEW;
		}
		model.addAttribute("receiver", receiver);
		return "admin/admin/receiver/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(AdminReceiver receiver, Long id, Long areaId, RedirectAttributes redirectAttributes) {
		receiver.setArea(areaService.find(areaId));
		if (!isValid(receiver)) {
			return ERROR_VIEW;
		}
		AdminReceiver pReceiver = adminReceiverService.find(id);
		if (pReceiver == null) {
			return ERROR_VIEW;
		}
		Admin admin = adminService.getCurrent();
		if (!admin.equals(pReceiver.getAdmin())) {
			return ERROR_VIEW;
		}
		adminReceiverService.update(receiver, "member");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody
	Message delete(Long id) {
		AdminReceiver receiver = adminReceiverService.find(id);
		if (receiver == null) {
			return ERROR_MESSAGE;
		}
		Admin admin = adminService.getCurrent();
		if (!admin.equals(receiver.getAdmin())) {
			return ERROR_MESSAGE;
		}
		adminReceiverService.delete(id);
		return SUCCESS_MESSAGE;
	}

}
