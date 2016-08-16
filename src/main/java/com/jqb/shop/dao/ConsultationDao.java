/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao;

import java.util.List;

import com.jqb.shop.Filter;
import com.jqb.shop.Order;
import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.entity.Consultation;
import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.Product;

/**
 * Dao - 咨询
 * 
 * @author JQB Team
 * @version 3.0
 */
public interface ConsultationDao extends BaseDao<Consultation, Long> {

	/**
	 * 查找咨询
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param isShow
	 *            是否显示
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 咨询
	 */
	List<Consultation> findList(Member member, Product product, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders);

	/**
	 * 查找咨询分页
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param isShow
	 *            是否显示
	 * @param pageable
	 *            分页信息
	 * @return 咨询分页
	 */
	Page<Consultation> findPage(Member member, Product product, Boolean isShow, Pageable pageable);

	/**
	 * 查找咨询数量
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param isShow
	 *            是否显示
	 * @return 咨询数量
	 */
	Long count(Member member, Product product, Boolean isShow);

}
