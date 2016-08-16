/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao.impl;

import com.jqb.shop.dao.ReturnsDao;
import com.jqb.shop.dao.ReturnsItemDao;
import com.jqb.shop.entity.Returns;
import com.jqb.shop.entity.ReturnsItem;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Dao - 退货单
 * 
 * @author JQB Team
 * @version 3.0
 */
@Repository("returnsItemDaoImpl")
public class ReturnsItemDaoImpl extends BaseDaoImpl<ReturnsItem, Long> implements ReturnsItemDao {

}
