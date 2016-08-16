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

import com.jqb.shop.dao.AdminDao;
import com.jqb.shop.entity.Admin;

import org.springframework.stereotype.Repository;

/**
 * Dao - 管理员
 *
 * @author JQB Team
 * @version 3.0
 */
@Repository("adminDaoImpl")
public class AdminDaoImpl extends BaseDaoImpl<Admin, Long> implements AdminDao {

	public boolean usernameExists(String username) {
		if (username == null) {
			return false;
		}
		String jpql = "select count(*) from Admin admin where lower(admin.username) = lower(:username)";
		Long count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT).setParameter("username", username).getSingleResult();
		return count > 0;
	}

	public Admin findByUsername(String username) {
		if (username == null) {
			return null;
		}
		try {
			String jpql = "select admin from Admin admin where lower(admin.username) = lower(:username)";
			return entityManager.createQuery(jpql, Admin.class).setFlushMode(FlushModeType.COMMIT).setParameter("username", username).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}