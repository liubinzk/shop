/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao.impl;

import com.jqb.shop.Filter;
import com.jqb.shop.dao.OrderItemDao;
import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.Order;
import com.jqb.shop.entity.OrderItem;

import org.springframework.stereotype.Repository;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Dao - 订单项
 * 
 * @author JQB Team
 * @version 3.0
 */
@Repository("orderItemDaoImpl")
public class OrderItemDaoImpl extends BaseDaoImpl<OrderItem, Long> implements OrderItemDao {

    /**
     * 根据商户计算申请退货成本价总价 状态（未确认  已确认  已完成 )
     *
     * @param
     *
     * @return
     */
    public List<OrderItem> findList(Member member, Integer count, List<Filter> filters, List<com.jqb.shop.Order> orders){
        List<OrderItem> resultList = null;
        String jpql = "select orderItem from OrderItem as orderItem  JOIN orderItem.order as ordertb where orderItem.order =ordertb  and ordertb.member.id=:memberId ";
        if (member == null) {
            return Collections.<OrderItem> emptyList();
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderItem> criteriaQuery = criteriaBuilder.createQuery(OrderItem.class);
        Root<OrderItem> root = criteriaQuery.from(OrderItem.class);
        criteriaQuery.select(root);
        Predicate restrictions = criteriaBuilder.conjunction();
        restrictions = criteriaBuilder.and(restrictions,criteriaBuilder.equal(root.get("order").get("member").get("id"), member.getId()) );
//        restrictions = criteriaBuilder.and(restrictions,criteriaBuilder.notEqual(root.get("order").get("orderStatus"), Order.OrderStatus.deleted) );
//        restrictions = criteriaBuilder.and(restrictions,criteriaBuilder.notEqual(root.get("order").get("orderStatus"), Order.OrderStatus.cancelled) );
        //restrictions = criteriaBuilder.and(restrictions,criteriaBuilder.or(restrictions, criteriaBuilder.notEqual(root.get("order").get("orderStatus"), Order.OrderStatus.cancelled), restrictions, criteriaBuilder.and(criteriaBuilder.equal(root.get("order").get("orderStatus"), Order.OrderStatus.deleted), criteriaBuilder.notEqual(root.get("order").get("paymentStatus"), Order.PaymentStatus.paid))));
        criteriaQuery.where(restrictions);
        return super.findList(criteriaQuery, null, count, filters, orders);
    }

}
