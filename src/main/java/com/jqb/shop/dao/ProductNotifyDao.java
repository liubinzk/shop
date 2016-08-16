/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao;

import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.Product;
import com.jqb.shop.entity.ProductNotify;

/**
 * Dao - 到货通知
 * 
 * @author JQB Team
 * @version 3.0
 */
public interface ProductNotifyDao extends BaseDao<ProductNotify, Long> {

	/**
	 * 判断到货通知是否存在
	 * 
	 * @param product
	 *            商品
	 * @param email
	 *            E-mail(忽略大小写)
	 * @return 到货通知是否存在
	 */
	boolean exists(Product product, String email);

	/**
	 * 查找到货通知分页
	 * 
	 * @param member
	 *            会员
	 * @param isMarketable
	 *            是否上架
	 * @param isOutOfStock
	 *            商品是否缺货
	 * @param hasSent
	 *            是否已发送.
	 * @param pageable
	 *            分页信息
	 * @return 到货通知分页
	 */
	Page<ProductNotify> findPage(Member member, Boolean isMarketable, Boolean isOutOfStock, Boolean hasSent, Pageable pageable);

	/**
	 * 查找到货通知数量
	 * 
	 * @param member
	 *            会员
	 * @param isMarketable
	 *            是否上架
	 * @param isOutOfStock
	 *            商品是否缺货
	 * @param hasSent
	 *            是否已发送.
	 * @return 到货通知数量
	 */
	Long count(Member member, Boolean isMarketable, Boolean isOutOfStock, Boolean hasSent);

}
