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
import com.jqb.shop.entity.Ad;
import com.jqb.shop.entity.Ad.Type;
import com.jqb.shop.service.AdPositionService;
import com.jqb.shop.service.AdService;

import com.jqb.shop.service.PromotionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller - 广告
 * 
 * @author JQB Team
 * @version 3.0
 */
@Controller("adminAdController")
@RequestMapping("/admin/ad")
public class AdController extends BaseController {

	@Resource(name = "adServiceImpl")
	private AdService adService;
	@Resource(name = "adPositionServiceImpl")
	private AdPositionService adPositionService;

	@Resource(name = "promotionServiceImpl")
	private PromotionService promotionService;

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap model) {
		model.addAttribute("promotions", promotionService.findList(true, false, 100, null, null));
		model.addAttribute("types", Type.values());
		model.addAttribute("adPositions", adPositionService.findAll());
		return "/admin/ad/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(Ad ad, Long adPositionId,Long[] promotionIds, RedirectAttributes redirectAttributes) {
		ad.setAdPosition(adPositionService.find(adPositionId));
		if (!isValid(ad)) {
			return ERROR_VIEW;
		}
		if (ad.getBeginDate() != null && ad.getEndDate() != null && ad.getBeginDate().after(ad.getEndDate())) {
			return ERROR_VIEW;
		}
		if (ad.getType() == Type.text) {
			ad.setPath(null);
		} else {
			ad.setContent(null);
		}
		StringBuffer adUrl = new StringBuffer("${basepath}/ad/show?");
		if(promotionIds != null && promotionIds.length >0){
			for(long promotionId : promotionIds){
				adUrl.append("&promotionId=" + promotionId);
			}
			;
		}
		ad.setUrl(adUrl.toString());
		adService.save(ad);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		model.addAttribute("promotions", promotionService.findList(true, false, 100, null, null));
		model.addAttribute("types", Type.values());
		model.addAttribute("ad", adService.find(id));
		model.addAttribute("adPositions", adPositionService.findAll());
		return "/admin/ad/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(Ad ad, Long adPositionId,Long[] promotionIds, RedirectAttributes redirectAttributes) {
		ad.setAdPosition(adPositionService.find(adPositionId));
		if (!isValid(ad)) {
			return ERROR_VIEW;
		}
		if (ad.getBeginDate() != null && ad.getEndDate() != null && ad.getBeginDate().after(ad.getEndDate())) {
			return ERROR_VIEW;
		}
		if (ad.getType() == Type.text) {
			ad.setPath(null);
		} else {
			ad.setContent(null);
		}
		StringBuffer prommotionIdStr = new StringBuffer("");
		StringBuffer adUrl = new StringBuffer("${basepath}/ad/show?");
		if(promotionIds != null && promotionIds.length >0){
			for(long promotionId : promotionIds){
				adUrl.append("&promotionId=" + promotionId);
				prommotionIdStr.append(promotionId).append(",");
			}
		}
		//ad.setPromotionIds(prommotionIdStr.toString());
		ad.setUrl(adUrl.toString());
		adService.update(ad);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", adService.findPage(pageable));
		return "/admin/ad/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody
	Message delete(Long[] ids) {
		adService.delete(ids);
		return SUCCESS_MESSAGE;
	}

}
