/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.service;

import com.jqb.shop.entity.Cart;
import com.jqb.shop.entity.Member;

import java.util.List;

/**
 * Service - 购物车
 * 
 * @author JQB Team
 * @version 3.0
 */
public interface CartService extends BaseService<Cart, Long> {

	/**
	 * 获取当前购物车
	 * 
	 * @return 当前购物车,若不存在则返回null
	 */
	Cart getCurrent();

	/**
	 * 获取Mobile购物车
	 *
	 * @return 当前购物车,若不存在则返回null
	 */
	Cart getMobileCurrent();

	/**
	 * 合并临时购物车至会员
	 * 
	 * @param member
	 *            会员
	 * @param cart
	 *            临时购物车
	 */
	void merge(Member member, Cart cart);

	/**
	 * 清除过期购物车
	 */
	void evictExpired();

	public List<Cart> findExpired();

	public void evictExpired(long cartId, long memberId);

	public List<Cart> findDuplCart(long memberId);

}
