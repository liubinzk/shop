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
import com.jqb.shop.entity.Brand;

/**
 * Service - 品牌
 * 
 * @author JQB Team
 * @version 3.0
 */
public interface BrandService extends BaseService<Brand, Long> {

	/**
	 * 查找品牌(缓存)
	 * 
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param cacheRegion
	 *            缓存区域
	 * @return 品牌(缓存)
	 */
	List<Brand> findList(Integer count, List<Filter> filters, List<Order> orders, String cacheRegion);

}
