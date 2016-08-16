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

import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.dao.ReceiverDao;
import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.Receiver;
import com.jqb.shop.service.ReceiverService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service - 收货地址
 * 
 * @author JQB Team
 * @version 3.0
 */
@Service("receiverServiceImpl")
public class ReceiverServiceImpl extends BaseServiceImpl<Receiver, Long> implements ReceiverService {

	@Resource(name = "receiverDaoImpl")
	private ReceiverDao receiverDao;

	@Resource(name = "receiverDaoImpl")
	public void setBaseDao(ReceiverDao receiverDao) {
		super.setBaseDao(receiverDao);
	}

	@Transactional(readOnly = true)
	public Receiver findDefault(Member member) {
		return receiverDao.findDefault(member);
	}

	@Transactional(readOnly = true)
	public Page<Receiver> findPage(Member member, Pageable pageable) {
		return receiverDao.findPage(member, pageable);
	}

	public List<Receiver> findList(String consignee){
		return receiverDao.findList(consignee);
	}

}
