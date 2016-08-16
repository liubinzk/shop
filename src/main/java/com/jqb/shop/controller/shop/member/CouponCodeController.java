/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.controller.shop.member;

import javax.annotation.Resource;

import com.jqb.shop.Message;
import com.jqb.shop.Pageable;
import com.jqb.shop.controller.shop.BaseController;
import com.jqb.shop.entity.Coupon;
import com.jqb.shop.entity.Member;
import com.jqb.shop.service.CouponCodeService;
import com.jqb.shop.service.CouponService;
import com.jqb.shop.service.MemberService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller - 会员中心 - 优惠码
 * 
 * @author JQB Team
 * @version 3.0
 */
@Controller("shopMemberCouponCodeController")
@RequestMapping("/member/coupon_code")
public class CouponCodeController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	@Resource(name = "couponServiceImpl")
	private CouponService couponService;
	@Resource(name = "couponCodeServiceImpl")
	private CouponCodeService couponCodeService;

	/**
	 * 兑换
	 */
	@RequestMapping(value = "/exchange", method = RequestMethod.GET)
	public String exchange(Integer pageNumber, ModelMap model) {
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		model.addAttribute("page", couponService.findPage(true, true, false, pageable));
		return "shop/member/coupon_code/exchange";
	}

	/**
	 * 兑换
	 */
	@RequestMapping(value = "/exchange", method = RequestMethod.POST)
	public @ResponseBody
	Message exchange(Long id) {
		Coupon coupon = couponService.find(id);
		if (coupon == null || !coupon.getIsEnabled() || !coupon.getIsExchange() || coupon.hasExpired()) {
			return ERROR_MESSAGE;
		}
		Member member = memberService.getCurrent();
		if (member.getPoint() < coupon.getPoint()) {
			return Message.warn("shop.member.couponCode.point");
		}
		couponCodeService.exchange(coupon, member);
		return SUCCESS_MESSAGE;
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Integer pageNumber, ModelMap model) {
		Member member = memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		model.addAttribute("page", couponCodeService.findPage(member, pageable));
		return "shop/member/coupon_code/list";
	}

}
