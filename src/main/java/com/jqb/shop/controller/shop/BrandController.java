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

import com.jqb.shop.Pageable;
import com.jqb.shop.ResourceNotFoundException;
import com.jqb.shop.entity.Brand;
import com.jqb.shop.service.BrandService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller - 品牌
 * 
 * @author JQB Team
 * @version 3.0
 */
@Controller("shopBrandController")
@RequestMapping("/brand")
public class BrandController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 40;

	@Resource(name = "brandServiceImpl")
	private BrandService brandService;

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list/{pageNumber}", method = RequestMethod.GET)
	public String list(@PathVariable Integer pageNumber, ModelMap model) {
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		model.addAttribute("page", brandService.findPage(pageable));
		return "/shop/brand/list";
	}

	/**
	 * 内容
	 */
	@RequestMapping(value = "/content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable Long id, ModelMap model) {
		Brand brand = brandService.find(id);
		if (brand == null) {
			throw new ResourceNotFoundException();
		}
		model.addAttribute("brand", brand);
		return "/shop/brand/content";
	}

}
