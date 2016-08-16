/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao;

import com.jqb.shop.Filter;
import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.OrderItem;

import java.util.List;

/**
 * Dao - 订单项
 * 
 * @author JQB Team
 * @version 3.0
 */
public interface OrderItemDao extends BaseDao<OrderItem, Long> {

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
    List<OrderItem> findList(Member member, Integer count, List<Filter> filters, List<com.jqb.shop.Order> orders);
}
