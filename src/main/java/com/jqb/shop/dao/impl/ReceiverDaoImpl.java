/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao.impl;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.dao.ReceiverDao;
import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.Receiver;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Dao - 收货地址
 * 
 * @author JQB Team
 * @version 3.0
 */
@Repository("receiverDaoImpl")
public class ReceiverDaoImpl extends BaseDaoImpl<Receiver, Long> implements ReceiverDao {

	public Receiver findDefault(Member member) {
		if (member == null) {
			return null;
		}
		try {
			String jpql = "select receiver from Receiver receiver where receiver.member = :member and receiver.isDefault = true";
			return entityManager.createQuery(jpql, Receiver.class).setFlushMode(FlushModeType.COMMIT).setParameter("member", member).getSingleResult();
		} catch (NoResultException e) {
			try {
				String jpql = "select receiver from Receiver receiver where receiver.member = :member order by receiver.modifyDate desc";
				return entityManager.createQuery(jpql, Receiver.class).setFlushMode(FlushModeType.COMMIT).setParameter("member", member).setMaxResults(1).getSingleResult();
			} catch (NoResultException e1) {
				return null;
			}
		}
	}

	public Page<Receiver> findPage(Member member, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Receiver> criteriaQuery = criteriaBuilder.createQuery(Receiver.class);
		Root<Receiver> root = criteriaQuery.from(Receiver.class);
		criteriaQuery.select(root);
		if (member != null) {
			criteriaQuery.where(criteriaBuilder.equal(root.get("member"), member));
		}
		return super.findPage(criteriaQuery, pageable);
	}

	/**
	 * 保存并处理默认
	 * 
	 * @param receiver
	 *            收货地址
	 */
	@Override
	public void persist(Receiver receiver) {
		Assert.notNull(receiver);
		Assert.notNull(receiver.getMember());
		if (receiver.getIsDefault()) {
			String jpql = "update Receiver receiver set receiver.isDefault = false where receiver.member = :member and receiver.isDefault = true";
			entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).setParameter("member", receiver.getMember()).executeUpdate();
		}
		super.persist(receiver);
	}

	/**
	 * 更新并处理默认
	 * 
	 * @param receiver
	 *            收货地址
	 * @return 收货地址
	 */
	@Override
	public Receiver merge(Receiver receiver) {
		Assert.notNull(receiver);
		Assert.notNull(receiver.getMember());
		if (receiver.getIsDefault()) {
			String jpql = "update Receiver receiver set receiver.isDefault = false where receiver.member = :member and receiver.isDefault = true and receiver != :receiver";
			entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).setParameter("member", receiver.getMember()).setParameter("receiver", receiver).executeUpdate();
		}
		return super.merge(receiver);
	}


	public List<Receiver> findList(String consignee) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Receiver> criteriaQuery = criteriaBuilder.createQuery(Receiver.class);
		Root<Receiver> root = criteriaQuery.from(Receiver.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (consignee != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("consignee"), consignee));
		}
		criteriaQuery.where(restrictions);
		return entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT).getResultList();
	}

}
