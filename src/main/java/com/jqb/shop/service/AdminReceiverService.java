/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.service;

import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.entity.Admin;
import com.jqb.shop.entity.AdminReceiver;
import com.jqb.shop.entity.Member;

/**
 * Service - 收货地址
 * 
 * @author JQB Team
 * @version 3.0
 */
public interface AdminReceiverService extends BaseService<AdminReceiver, Long> {

	/**
	 * 查找默认收货地址
	 * 
	 * @param admin
	 *            会员
	 * @return 默认收货地址，若不存在则返回最新收货地址
	 */
	AdminReceiver findDefault(Admin admin);

	/**
	 * 查找收货地址分页
	 * 
	 * @param admin
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 收货地址分页
	 */
	Page<AdminReceiver> findPage(Admin admin, Pageable pageable);

}
