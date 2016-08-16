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
import com.jqb.shop.dao.AdminReceiverDao;
import com.jqb.shop.dao.ReceiverDao;
import com.jqb.shop.entity.Admin;
import com.jqb.shop.entity.AdminReceiver;
import com.jqb.shop.entity.Member;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Dao - 收货地址
 * 
 * @author JQB Team
 * @version 3.0
 */
@Repository("adminReceiverDaoImpl")
public class AdminReceiverDaoImpl extends BaseDaoImpl<AdminReceiver, Long> implements AdminReceiverDao {

	public AdminReceiver findDefault(Admin admin) {
		if (admin == null) {
			return null;
		}
		try {
			String jpql = "select receiver from AdminReceiver receiver where receiver.admin = :admin and receiver.isDefault = true";
			return entityManager.createQuery(jpql, AdminReceiver.class).setFlushMode(FlushModeType.COMMIT).setParameter("admin", admin).getSingleResult();
		} catch (NoResultException e) {
			try {
				String jpql = "select receiver from AdminReceiver receiver where receiver.admin = :admin order by receiver.modifyDate desc";
				return entityManager.createQuery(jpql, AdminReceiver.class).setFlushMode(FlushModeType.COMMIT).setParameter("admin", admin).setMaxResults(1).getSingleResult();
			} catch (NoResultException e1) {
				return null;
			}
		}
	}

	public Page<AdminReceiver> findPage(Admin admin, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<AdminReceiver> criteriaQuery = criteriaBuilder.createQuery(AdminReceiver.class);
		Root<AdminReceiver> root = criteriaQuery.from(AdminReceiver.class);
		criteriaQuery.select(root);
		if (admin != null) {
			criteriaQuery.where(criteriaBuilder.equal(root.get("admin"), admin));
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
	public void persist(AdminReceiver receiver) {
		Assert.notNull(receiver);
		Assert.notNull(receiver.getAdmin());
		if (receiver.getIsDefault()) {
			String jpql = "update AdminReceiver receiver set receiver.isDefault = false where receiver.admin = :admin and receiver.isDefault = true";
			entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).setParameter("admin", receiver.getAdmin()).executeUpdate();
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
	public AdminReceiver merge(AdminReceiver receiver) {
		Assert.notNull(receiver);
		Assert.notNull(receiver.getAdmin());
		if (receiver.getIsDefault()) {
			String jpql = "update AdminReceiver receiver set receiver.isDefault = false where receiver.admin = :admin and receiver.isDefault = true and receiver != :receiver";
			entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).setParameter("admin", receiver.getAdmin()).setParameter("receiver", receiver).executeUpdate();
		}
		return super.merge(receiver);
	}

}
