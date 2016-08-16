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
import com.jqb.shop.entity.FriendLink;
import com.jqb.shop.entity.FriendLink.Type;

/**
 * Service - 友情链接
 * 
 * @author JQB Team
 * @version 3.0
 */
public interface FriendLinkService extends BaseService<FriendLink, Long> {

	/**
	 * 查找友情链接
	 * 
	 * @param type
	 *            类型
	 * @return 友情链接
	 */
	List<FriendLink> findList(Type type);

	/**
	 * 查找友情链接(缓存)
	 * 
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param cacheRegion
	 *            缓存区域
	 * @return 友情链接(缓存)
	 */
	List<FriendLink> findList(Integer count, List<Filter> filters, List<Order> orders, String cacheRegion);

}
