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
import java.util.Set;

import com.jqb.shop.entity.Parameter;
import com.jqb.shop.entity.ParameterGroup;

/**
 * Dao - 参数
 * 
 * @author JQB Team
 * @version 3.0
 */
public interface ParameterDao extends BaseDao<Parameter, Long> {

	/**
	 * 查找参数
	 * 
	 * @param parameterGroup
	 *            参数组
	 * @param excludes
	 *            排除参数
	 * @return 参数
	 */
	List<Parameter> findList(ParameterGroup parameterGroup, Set<Parameter> excludes);

}
