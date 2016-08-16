/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao.impl;

import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.dao.ReturnsDao;
import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.Order;
import com.jqb.shop.entity.Returns;
import com.jqb.shop.entity.Returns.ReturnsStatus;

import org.springframework.stereotype.Repository;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Dao - 退货单
 *
 * @author JQB Team
 * @version 3.0
 */
@Repository("returnsDaoImpl")
public class ReturnsDaoImpl extends BaseDaoImpl<Returns, Long> implements ReturnsDao {

    public List<Returns> findByOrderId(Long orderId) {
        if (orderId == null) {
            return null;
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Returns> criteriaQuery = criteriaBuilder.createQuery(Returns.class);
        Root<Returns> root = criteriaQuery.from(Returns.class);
        criteriaQuery.select(root);
        criteriaQuery.where(criteriaBuilder.equal(root.get("order"), orderId));
        return super.findList(criteriaQuery, null, 10, null, null);
    }
    public Returns findByOrderIdAndItemId(Long orderId, String itemSn){
        String jpql = "select returns from Returns as returns  left join returns.returnsItems as returnsItem where  lower(returns.order.id) = lower(:orderId) and returnsItem.sn=:itemSn ";
        try {
            return entityManager.createQuery(jpql, Returns.class).setFlushMode(FlushModeType.COMMIT).setParameter("orderId", orderId).setParameter("itemSn", itemSn).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Page<Returns> findPage(Pageable pageable, Member member){
        if (member == null) {
            return new Page<Returns>(Collections.<Returns> emptyList(), 0, pageable);
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Returns> criteriaQuery = criteriaBuilder.createQuery(Returns.class);
        Root<Returns> root = criteriaQuery.from(Returns.class);
        criteriaQuery.select(root);
        //criteriaBuilder.equal(root.get("isMarketable"), isMarketable)
        criteriaQuery.where(criteriaBuilder.equal(root.get("order").get("member"), member.getId() ));
        return super.findPage(criteriaQuery, pageable);
    }
	/**
	 * 根据商户计算申请退货成本价总价 状态（未确认  已确认  已完成 )
	 * 
	 * @param commercialId
	 *            商户id
	 * @return 申请退货成本总价，若不存在则返回null
	 */
	public BigDecimal sumAmountByCommercial(Long commercialId) {
		BigDecimal result=BigDecimal.ZERO;
		if(commercialId==null){
			return result;
		}
		String jpql = "select sum(item.cost) from Returns returns JOIN returns.returnsItems item where returns.order.commercial.id =:commercialId and returns.returnsStatus in ("+ReturnsStatus.unconfirmed.ordinal()+","+ReturnsStatus.confirmed.ordinal()+","+ReturnsStatus.completed.ordinal()+")";
		try {
			result= entityManager.createQuery(jpql,BigDecimal.class).setFlushMode(FlushModeType.COMMIT).setParameter("commercialId", commercialId).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
		if(	result==null){
			result=BigDecimal.ZERO;
		}
		return result;
	}
}
