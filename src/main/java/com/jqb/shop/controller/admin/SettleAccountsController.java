/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.controller.admin;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jqb.shop.Filter;
import com.jqb.shop.Filter.Operator;
import com.jqb.shop.Message;
import com.jqb.shop.Pageable;
import com.jqb.shop.controller.shop.BaseController;
import com.jqb.shop.entity.Admin;
import com.jqb.shop.entity.SettleAccounts;
import com.jqb.shop.entity.SettleAccounts.Method;
import com.jqb.shop.entity.SettleAccounts.Status;
import com.jqb.shop.entity.SettleAccounts.Type;
import com.jqb.shop.service.AdminService;
import com.jqb.shop.service.CommercialService;
import com.jqb.shop.service.OrderService;
import com.jqb.shop.service.ReturnsService;
import com.jqb.shop.service.SettleAccountsService;

/**
 * Controller - 商户结算
 * 
 * @author JQB Team
 * @version 3.0
 */
@Controller("settleAccountsController")
@RequestMapping("/admin/settleAccounts")
public class SettleAccountsController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 50;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;
	@Resource(name = "settleAccountsServiceImpl")
	private SettleAccountsService settleAccountsService;
	@Resource(name = "commercialServiceImpl")
	private CommercialService commercialService;

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		Admin admin = adminService.getCurrent();
		pageable.getFilters().add(this.filterCurrentAdminByCommercial());
		model.addAttribute("page", settleAccountsService.findPage( pageable));
		model.addAttribute("admin", admin);
		return "admin/settle_accounts/list";
	}

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add( ModelMap model) {
		model.addAttribute("sumAmout", getSumAmout().doubleValue());//可提取额度=订单总额-退货申请总额
		return "/admin/settle_accounts/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(SettleAccounts settleAccounts, RedirectAttributes redirectAttributes) {

		Admin admin=adminService.getCurrent();
		settleAccounts.setCommercial(admin.getCommercial());
		settleAccounts.setAdmin(admin);
		settleAccounts.setMethod(Method.offline);
		settleAccounts.setStatus(Status.wait);
		settleAccounts.setType(Type.settleAccounts);
		settleAccounts.setFee(BigDecimal.ZERO);
		if (!isValid(settleAccounts)) {
			return ERROR_VIEW;
		}
		BigDecimal sumAmout=getSumAmout();
		if(sumAmout.compareTo(settleAccounts.getAmount())<0){
			addFlashMessage(redirectAttributes, Message.error("余额不足"));
		}else{
			settleAccountsService.save(settleAccounts);
			addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		}
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		model.addAttribute("settleAccounts", settleAccountsService.find(id));
		return "/admin/settle_accounts/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(SettleAccounts settleAccounts, RedirectAttributes redirectAttributes) {
//		SettleAccounts updateEntry=settleAccountsService.find(settleAccounts.getId());
//		if (!isValid(settleAccounts)) {
//			return ERROR_VIEW;
//		}
		//settleAccountsService.update(updateEntry);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 审核
	 */
	@RequestMapping(value = "/audit", method = RequestMethod.POST)
	public @ResponseBody
	Message audit(Long id) {
		Admin admin=adminService.getCurrent();
		if(!admin.getUsername().equalsIgnoreCase("admin")){//目前只有admin管理员才有权限
			return Message.error("不是超级管理员，无权限");
		}
		try {
			settleAccountsService.audit(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return Message.error(e.getMessage());
		}
		return SUCCESS_MESSAGE;
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody
	Message delete(Long[] ids) {
//		try {
//			for(Long id: ids){
//				commercialService.deleteAndCheck(id);
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//			return Message.error(e.getMessage());
//		}
		return SUCCESS_MESSAGE;
	}
	private Filter filterCurrentAdminByCommercial(){
		Admin admin=adminService.getCurrent();
		 Filter filter=new Filter();
		 if(admin.getCommercial()!= null){//商户null代表超级管理员
			 filter=new Filter("commercial",Operator.eq,admin.getCommercial().getId());
		}
		return filter;
	}
	private Long getCommercialByCurrentAdmin(){
		Admin admin=adminService.getCurrent();
		 if(admin.getCommercial()!= null){//商户null代表超级管理员
			 return admin.getCommercial().getId();
		}
		return null;
	}
	//可提取额度=订单总额-退货申请总额-已经申请总额
	private BigDecimal getSumAmout() {
		BigDecimal sumAmoutOrder=settleAccountsService.sumAmountByOrderCommercial(getCommercialByCurrentAdmin());
		BigDecimal sumAmoutReturns=settleAccountsService.sumAmountByReturnsCommercial(getCommercialByCurrentAdmin());
		BigDecimal sumAmout=settleAccountsService.sumAmountByCommercial(getCommercialByCurrentAdmin());
		return  sumAmoutOrder.subtract(sumAmoutReturns).subtract(sumAmout);
	}
}
