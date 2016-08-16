/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao;

import com.jqb.shop.entity.Cart;
import com.jqb.shop.entity.Member;

import java.util.List;

/**
 * Dao - 购物车
 * 
 * @author JQB Team
 * @version 3.0
 */
public interface CartDao extends BaseDao<Cart, Long> {

	/**
	 * 清除过期购物车
	 */
	void evictExpired();

	public void evictExpired(long cartId);

	public List<Cart> findExpired();
	public List<Cart> findDuplCart(long memberId);

}
