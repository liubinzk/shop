/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.jqb.shop.Filter;
import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.dao.OrderDao;
import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.Order;
import com.jqb.shop.entity.Order.OrderStatus;
import com.jqb.shop.entity.Order.PaymentStatus;
import com.jqb.shop.entity.Order.ShippingStatus;
import com.jqb.shop.entity.OrderItem;
import com.jqb.shop.entity.Product;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Repository;

/**
 * Dao - 订单
 * 
 * @author JQB Team
 * @version 3.0
 */
@Repository("orderDaoImpl")
public class OrderDaoImpl extends BaseDaoImpl<Order, Long> implements OrderDao {

	public Order findBySn(String sn) {
		if (sn == null) {
			return null;
		}
		String jpql = "select orders from Order orders where lower(orders.sn) = lower(:sn)";
		try {
			return entityManager.createQuery(jpql, Order.class).setFlushMode(FlushModeType.COMMIT).setParameter("sn", sn).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	/**
	 * 根据商户计算成本价总价 状态（已完成	已支付	已发货）
	 * 
	 * @param commercialId
	 *            商户id
	 * @return 成本总价，若不存在则返回null
	 */
	public BigDecimal sumAmountByCommercial(Long commercialId) {
		BigDecimal result=BigDecimal.ZERO;
		if(commercialId==null){
			return result;
		}

		String jpql = "select sum(item.cost) from Order orders JOIN orders.orderItems item where orders.commercial.id =:commercialId and orders.orderStatus="+OrderStatus.completed.ordinal()+" and orders.paymentStatus="+PaymentStatus.paid.ordinal()+" and orders.shippingStatus="+ShippingStatus.shipped.ordinal();
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
	public List<Order> findList(Member member, Integer count, List<Filter> filters, List<com.jqb.shop.Order> orders) {
		if (member == null) {
			return Collections.<Order> emptyList();
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.get("member"), member));
		return super.findList(criteriaQuery, null, count, filters, orders);
	}

	public Page<Order> findPage(Member member, Pageable pageable) {
		if (member == null) {
			return new Page<Order>(Collections.<Order> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions,criteriaBuilder.notEqual(root.get("orderStatus"), OrderStatus.deleted) );
		restrictions = criteriaBuilder.and(restrictions,criteriaBuilder.equal(root.get("member"), member) );
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Order> findPage(Member member,OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();

		restrictions = criteriaBuilder.and(restrictions,criteriaBuilder.equal(root.get("member"), member) );

		if (orderStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("orderStatus"), orderStatus));
		}
		if (paymentStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("paymentStatus"), paymentStatus));
		}
		if (shippingStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("shippingStatus"), shippingStatus));
		}
		if (hasExpired != null) {
			if (hasExpired) {
				restrictions = criteriaBuilder.and(restrictions, root.get("expire").isNotNull(), criteriaBuilder.lessThan(root.<Date> get("expire"), new Date()));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("expire").isNull(), criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("expire"), new Date())));
			}
		}
		restrictions = criteriaBuilder.and(restrictions,criteriaBuilder.notEqual(root.get("orderStatus"), OrderStatus.deleted) );
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Order> findPage(OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (orderStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("orderStatus"), orderStatus));
		}
		if (paymentStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("paymentStatus"), paymentStatus));
		}
		if (shippingStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("shippingStatus"), shippingStatus));
		}
		if (hasExpired != null) {
			if (hasExpired) {
				restrictions = criteriaBuilder.and(restrictions, root.get("expire").isNotNull(), criteriaBuilder.lessThan(root.<Date> get("expire"), new Date()));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("expire").isNull(), criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("expire"), new Date())));
			}
		}
		restrictions = criteriaBuilder.and(restrictions,criteriaBuilder.notEqual(root.get("orderStatus"), OrderStatus.deleted) );
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Long count(OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (orderStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("orderStatus"), orderStatus));
		}
		if (paymentStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("paymentStatus"), paymentStatus));
		}
		if (shippingStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("shippingStatus"), shippingStatus));
		}
		if (hasExpired != null) {
			if (hasExpired) {
				restrictions = criteriaBuilder.and(restrictions, root.get("expire").isNotNull(), criteriaBuilder.lessThan(root.<Date> get("expire"), new Date()));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("expire").isNull(), criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("expire"), new Date())));
			}
		}
		criteriaQuery.where(restrictions);
		return super.count(criteriaQuery, null);
	}

	public Long waitingPaymentCount(Member member) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.notEqual(root.get("orderStatus"), OrderStatus.completed), criteriaBuilder.notEqual(root.get("orderStatus"), OrderStatus.cancelled));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.equal(root.get("paymentStatus"), PaymentStatus.unpaid), criteriaBuilder.equal(root.get("paymentStatus"), PaymentStatus.partialPayment)));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("expire").isNull(), criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("expire"), new Date())));
		if (member != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		}
		criteriaQuery.where(restrictions);
		return super.count(criteriaQuery, null);
	}

	public Long waitingShippingCount(Member member) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.notEqual(root.get("orderStatus"), OrderStatus.completed), criteriaBuilder.notEqual(root.get("orderStatus"), OrderStatus.cancelled), criteriaBuilder.equal(root.get("paymentStatus"), PaymentStatus.paid), criteriaBuilder.equal(root.get("shippingStatus"), ShippingStatus.unshipped));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("expire").isNull(), criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("expire"), new Date())));
		if (member != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		}
		criteriaQuery.where(restrictions);
		return super.count(criteriaQuery, null);
	}

	public BigDecimal getSalesAmount(Date beginDate, Date endDate) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<BigDecimal> criteriaQuery = criteriaBuilder.createQuery(BigDecimal.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(criteriaBuilder.sum(root.<BigDecimal> get("amountPaid")));
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("orderStatus"), OrderStatus.completed));
		if (beginDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), beginDate));
		}
		if (endDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(root.<Date>get("createDate"), endDate));
		}
		criteriaQuery.where(restrictions);
		return entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT).getSingleResult();
	}

	public Integer getSalesVolume(Date beginDate, Date endDate) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = criteriaBuilder.createQuery(Integer.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(criteriaBuilder.sum(root.join("orderItems").<Integer> get("shippedQuantity")));
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("orderStatus"), OrderStatus.completed));
		if (beginDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), beginDate));
		}
		if (endDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(root.<Date> get("createDate"), endDate));
		}
		criteriaQuery.where(restrictions);
		return entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT).getSingleResult();
	}

	public void releaseStock() {
		String jpql = "select orders from Order orders where orders.isAllocatedStock = :isAllocatedStock and orders.expire is not null and orders.expire <= :now";
		List<Order> orders = entityManager.createQuery(jpql, Order.class).setParameter("isAllocatedStock", true).setParameter("now", new Date()).getResultList();
		if (orders != null) {
			for (Order order : orders) {
				if (order != null && order.getOrderItems() != null) {
					for (OrderItem orderItem : order.getOrderItems()) {
						if (orderItem != null) {
							Product product = orderItem.getProduct();
							if (product != null) {
								entityManager.lock(product, LockModeType.PESSIMISTIC_WRITE);
								product.setAllocatedStock(product.getAllocatedStock() - (orderItem.getQuantity() - orderItem.getShippedQuantity()));
							}
						}
					}
					order.setIsAllocatedStock(false);
				}
			}
		}
	}
	public List<List<String>> execute(String sql){
		List<List<String>> result = null;
		try {
			result= entityManager.createNativeQuery(sql).setFlushMode(FlushModeType.COMMIT).getResultList();
		} catch (NoResultException e) {
			return null;
		}
		return result;
	}

}
