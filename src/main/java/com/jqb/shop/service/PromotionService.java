/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.service;

import java.util.List;

import com.jqb.shop.Filter;
import com.jqb.shop.Order;
import com.jqb.shop.entity.Promotion;

/**
 * Service - 促销
 * 
 * @author JQB Team
 * @version 3.0
 */
public interface PromotionService extends BaseService<Promotion, Long> {

	/**
	 * 查找促销
	 * 
	 * @param hasBegun
	 *            是否已开始
	 * @param hasEnded
	 *            是否已结束
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 促销
	 */
	List<Promotion> findList(Boolean hasBegun, Boolean hasEnded, Integer count, List<Filter> filters, List<Order> orders);

	/**
	 * 查找促销(缓存)
	 * 
	 * @param hasBegun
	 *            是否已开始
	 * @param hasEnded
	 *            是否已结束
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param cacheRegion
	 *            缓存区域
	 * @return 促销(缓存)
	 */
	List<Promotion> findList(Boolean hasBegun, Boolean hasEnded, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion);

}
