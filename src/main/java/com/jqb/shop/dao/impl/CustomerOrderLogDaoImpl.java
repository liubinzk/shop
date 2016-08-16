/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao.impl;

import com.jqb.shop.dao.CustomerOrderLogDao;
import com.jqb.shop.dao.OrderLogDao;
import com.jqb.shop.entity.CustomerOrderLog;
import com.jqb.shop.entity.OrderLog;
import org.springframework.stereotype.Repository;

/**
 * Dao - 订单日志
 * 
 * @author JQB Team
 * @version 3.0
 */
@Repository("customerOrderLogDaoImpl")
public class CustomerOrderLogDaoImpl extends BaseDaoImpl<CustomerOrderLog, Long> implements CustomerOrderLogDao {

}
