/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao.impl;

import com.jqb.shop.dao.AreaDao;
import com.jqb.shop.dao.TsAreaDao;
import com.jqb.shop.entity.Area;
import com.jqb.shop.entity.TsArea;
import org.springframework.stereotype.Repository;

import javax.persistence.FlushModeType;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Dao - 地区
 * 
 * @author JQB Team
 * @version 3.0
 */
@Repository("tsAreaDaoImpl")
public class TsAreaDaoImpl extends BaseDaoImpl<TsArea, Long> implements TsAreaDao {

	public List<TsArea> findRoots(Integer count) {
		String jpql = "select tsarea from TsArea tsarea where tsarea.pid=0 order by tsarea.orders asc";
		TypedQuery<TsArea> query = entityManager.createQuery(jpql, TsArea.class).setFlushMode(FlushModeType.COMMIT);
		if (count != null) {
			query.setMaxResults(count);
		}
		return query.getResultList();
	}

}
