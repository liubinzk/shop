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

import com.jqb.shop.dao.SeoDao;
import com.jqb.shop.entity.Seo;
import com.jqb.shop.entity.Seo.Type;

import org.springframework.stereotype.Repository;

/**
 * Dao - SEO设置
 * 
 * @author JQB Team
 * @version 3.0
 */
@Repository("seoDaoImpl")
public class SeoDaoImpl extends BaseDaoImpl<Seo, Long> implements SeoDao {

	public Seo find(Type type) {
		if (type == null) {
			return null;
		}
		try {
			String jpql = "select seo from Seo seo where seo.type = :type";
			return entityManager.createQuery(jpql, Seo.class).setFlushMode(FlushModeType.COMMIT).setParameter("type", type).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
