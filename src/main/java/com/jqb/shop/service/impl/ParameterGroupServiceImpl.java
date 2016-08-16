/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.service.impl;

import javax.annotation.Resource;

import com.jqb.shop.dao.ParameterGroupDao;
import com.jqb.shop.entity.ParameterGroup;
import com.jqb.shop.service.ParameterGroupService;

import org.springframework.stereotype.Service;

/**
 * Service - 参数组
 * 
 * @author JQB Team
 * @version 3.0
 */
@Service("parameterGroupServiceImpl")
public class ParameterGroupServiceImpl extends BaseServiceImpl<ParameterGroup, Long> implements ParameterGroupService {

	@Resource(name = "parameterGroupDaoImpl")
	public void setBaseDao(ParameterGroupDao parameterGroupDao) {
		super.setBaseDao(parameterGroupDao);
	}

}
