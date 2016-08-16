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
import com.jqb.shop.dao.CartDao;
import com.jqb.shop.dao.ReturnsDao;
import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.Returns;
import com.jqb.shop.service.ReturnsService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service - 退货单
 * 
 * @author JQB Team
 * @version 3.0
 */
@Service("returnsServiceImpl")
public class ReturnsServiceImpl extends BaseServiceImpl<Returns, Long> implements ReturnsService {

	@Resource(name = "returnsDaoImpl")
	private ReturnsDao returnsDao;

	@Resource(name = "returnsDaoImpl")
	public void setBaseDao(ReturnsDao returnsDao) {
		super.setBaseDao(returnsDao);
	}


	public List<Returns> findByOrderId(Long orderId) {
		return returnsDao.findByOrderId(orderId);
	}

	public Returns findByOrderIdAndItemId(Long orderId, String itemSn) {
		return returnsDao.findByOrderIdAndItemId(orderId, itemSn);
	}

	@Transactional(readOnly = true)
	public Page<Returns> findPage(Pageable pageable,  Member member) {
		return returnsDao.findPage(pageable, member);
	}


}
