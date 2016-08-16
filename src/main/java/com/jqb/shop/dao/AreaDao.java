/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao;

import java.util.List;

import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.entity.Area;

/**
 * Dao - 地区
 * 
 * @author JQB Team
 * @version 3.0
 */
public interface AreaDao extends BaseDao<Area, Long> {

	/**
	 * 查找顶级地区
	 * 
	 * @param count
	 *            数量
	 * @return 顶级地区
	 */
	List<Area> findRoots(Integer count);

	public Page<Area> findLvl2Page( Pageable pageable);

}
