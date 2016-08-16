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

import com.jqb.shop.dao.ShippingMethodDao;
import com.jqb.shop.entity.ShippingMethod;
import com.jqb.shop.service.ShippingMethodService;

import org.springframework.stereotype.Service;

/**
 * Service - 配送方式
 * 
 * @author JQB Team
 * @version 3.0
 */
@Service("shippingMethodServiceImpl")
public class ShippingMethodServiceImpl extends BaseServiceImpl<ShippingMethod, Long> implements ShippingMethodService {

	@Resource(name = "shippingMethodDaoImpl")
	public void setBaseDao(ShippingMethodDao shippingMethodDao) {
		super.setBaseDao(shippingMethodDao);
	}

}
