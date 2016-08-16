/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao;

import com.jqb.shop.entity.Area;
import com.jqb.shop.entity.TsArea;

import java.util.List;

/**
 * Dao - 地区
 * 
 * @author JQB Team
 * @version 3.0
 */
public interface TsAreaDao extends BaseDao<TsArea, Long> {

	/**
	 * 查找顶级地区
	 * 
	 * @param count
	 *            数量
	 * @return 顶级地区
	 */
	List<TsArea> findRoots(Integer count);


}
