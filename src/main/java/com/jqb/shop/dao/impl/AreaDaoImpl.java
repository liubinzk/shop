/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao.impl;

import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.dao.AreaDao;
import com.jqb.shop.entity.Area;

import com.jqb.shop.entity.Returns;
import org.springframework.stereotype.Repository;

/**
 * Dao - 地区
 * 
 * @author JQB Team
 * @version 3.0
 */
@Repository("areaDaoImpl")
public class AreaDaoImpl extends BaseDaoImpl<Area, Long> implements AreaDao {

	public List<Area> findRoots(Integer count) {
		String jpql = "select area from Area area where area.parent is null order by area.order asc";
		TypedQuery<Area> query = entityManager.createQuery(jpql, Area.class).setFlushMode(FlushModeType.COMMIT);
		if (count != null) {
			query.setMaxResults(count);
		}
		return query.getResultList();
	}
	public Page<Area> findLvl2Page(Pageable pageable){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Area> criteriaQuery = criteriaBuilder.createQuery(Area.class);
		Root<Area> root = criteriaQuery.from(Area.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(criteriaBuilder.isNull(root.get("parent").get("parent")));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.and(criteriaBuilder.isNotNull(root.get("path"))));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isMarketable"), true));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

}
