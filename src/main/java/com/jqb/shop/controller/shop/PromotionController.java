/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.controller.shop;

import javax.annotation.Resource;

import com.jqb.shop.ResourceNotFoundException;
import com.jqb.shop.entity.Promotion;
import com.jqb.shop.service.PromotionService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller - 促销
 * 
 * @author JQB Team
 * @version 3.0
 */
@Controller("shopPromotionController")
@RequestMapping("/promotion")
public class PromotionController extends BaseController {

	@Resource(name = "promotionServiceImpl")
	private PromotionService promotionService;

	/**
	 * 内容
	 */
	@RequestMapping(value = "/content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable Long id, ModelMap model) {
		Promotion promotion = promotionService.find(id);
		if (promotion == null) {
			throw new ResourceNotFoundException();
		}
		model.addAttribute("promotion", promotion);
		return "/shop/promotion/content";
	}

}
