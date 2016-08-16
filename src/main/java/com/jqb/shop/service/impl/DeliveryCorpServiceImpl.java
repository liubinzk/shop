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

import com.jqb.shop.dao.DeliveryCorpDao;
import com.jqb.shop.entity.DeliveryCorp;
import com.jqb.shop.service.DeliveryCorpService;

import org.springframework.stereotype.Service;

/**
 * Service - 物流公司
 * 
 * @author JQB Team
 * @version 3.0
 */
@Service("deliveryCorpServiceImpl")
public class DeliveryCorpServiceImpl extends BaseServiceImpl<DeliveryCorp, Long> implements DeliveryCorpService {

	@Resource(name = "deliveryCorpDaoImpl")
	public void setBaseDao(DeliveryCorpDao deliveryCorpDao) {
		super.setBaseDao(deliveryCorpDao);
	}

}
