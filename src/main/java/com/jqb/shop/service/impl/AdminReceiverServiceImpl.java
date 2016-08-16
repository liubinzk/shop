/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.service.impl;

import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.dao.AdminReceiverDao;
import com.jqb.shop.dao.ReceiverDao;
import com.jqb.shop.entity.Admin;
import com.jqb.shop.entity.AdminReceiver;
import com.jqb.shop.service.AdminReceiverService;
import com.jqb.shop.service.ReceiverService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Service - 收货地址
 * 
 * @author JQB Team
 * @version 3.0
 */
@Service("adminReceiverServiceImpl")
public class AdminReceiverServiceImpl extends BaseServiceImpl<AdminReceiver, Long> implements AdminReceiverService {

	@Resource(name = "adminReceiverDaoImpl")
	private AdminReceiverDao adminReceiverDao;

	@Resource(name = "adminReceiverDaoImpl")
	public void setBaseDao(AdminReceiverDao receiverDao) {
		super.setBaseDao(adminReceiverDao);
	}

	@Transactional(readOnly = true)
	public AdminReceiver findDefault(Admin admin) {
		return adminReceiverDao.findDefault(admin);
	}

	@Transactional(readOnly = true)
	public Page<AdminReceiver> findPage(Admin admin, Pageable pageable) {
		return adminReceiverDao.findPage(admin, pageable);
	}

}
