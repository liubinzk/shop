/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.jqb.shop.dao.CartDao;
import com.jqb.shop.entity.Cart;

import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.Product;
import com.jqb.shop.util.DateFormateUtil;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Repository;

/**
 * Dao - 购物车
 * 
 * @author JQB Team
 * @version 3.0
 */
@Repository("cartDaoImpl")
public class CartDaoImpl extends BaseDaoImpl<Cart, Long> implements CartDao {

	public void evictExpired() {
		String jpql = "delete from Cart cart where cart.modifyDate <= :expire and cart.member is null ";
		entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).setParameter("expire",  DateUtils.addSeconds(new Date(), -Cart.TIMEOUT) ).executeUpdate();
	}

	public void evictExpired(long cartId) {
		String del_cartItem = "delete from CartItem cartitem where cartitem.cart.id = :cartId  ";
		entityManager.createQuery(del_cartItem).setFlushMode(FlushModeType.COMMIT).setParameter("cartId",  cartId ).executeUpdate();

		String jpql = "delete from Cart cart where cart.id = :cartId ";
		entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).setParameter("cartId",  cartId ).executeUpdate();

	}

	public List<Cart> findExpired(){
		String jpql = "select cart from Cart cart where cart.modifyDate <= :expire ";
		return (List<Cart>)entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).setParameter("expire",  DateUtils.addSeconds(new Date(), -Cart.TIMEOUT) ).getResultList();
	}

	public List<Cart> findDuplCart(long memberId){
		String jpql = "select cart from Cart cart where cart.member.id = :memberId ";
		return (List<Cart>)entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).setParameter("memberId",  memberId ).getResultList();
	}

}
