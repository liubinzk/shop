/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.job;

import javax.annotation.Resource;

import com.jqb.shop.entity.Cart;
import com.jqb.shop.service.CartService;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

/**
 * Job - 购物车
 * 
 * @author JQB Team
 * @version 3.0
 */
@Component("cartJob")
@Lazy(false)
public class CartJob {

	@Resource(name = "cartServiceImpl")
	private CartService cartService;

	/** logger */
	private static final Logger logger = Logger.getLogger(CartJob.class.getName());

	/**
	 * 清除过期购物车
	 */
	@Scheduled(cron = "${job.cart_evict_expired.cron}")
	public void evictExpired() {
		List<Cart> cartList = cartService.findExpired();
		if(cartList != null){
			for (Cart cart : cartList) {
				long memberId=0;
				if(cart.getMember() != null && !"".equals(cart.getMember())){
					memberId = cart.getMember().getId();
				}
				cartService.evictExpired(cart.getId(), memberId);
			}
		}
	}

	private void eviceDuplCart(){
//		Filter filter = new Filter();
//		cartService.findList()
	}

}
