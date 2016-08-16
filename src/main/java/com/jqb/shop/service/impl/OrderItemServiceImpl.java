/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.service.impl;

import javax.annotation.Resource;

import com.jqb.shop.Filter;
import com.jqb.shop.dao.OrderItemDao;
import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.OrderItem;
import com.jqb.shop.service.OrderItemService;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service - 订单项
 * 
 * @author JQB Team
 * @version 3.0
 */
@Service("orderItemServiceImpl")
public class OrderItemServiceImpl extends BaseServiceImpl<OrderItem, Long> implements OrderItemService {

	@Resource(name = "orderItemDaoImpl")
	public void setBaseDao(OrderItemDao orderItemDao) {
		super.setBaseDao(orderItemDao);
	}

	@Resource(name = "orderItemDaoImpl")
	private OrderItemDao orderItemDao;

	/**
	 * 查找订单
	 *
	 * @param member
	 *            会员
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 订单
	 */
	public List<OrderItem> findList(Member member, Integer count, List<Filter> filters, List<com.jqb.shop.Order> orders){
		return orderItemDao.findList(member,count,filters,orders);
	}

}
