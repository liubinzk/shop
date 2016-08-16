/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao;

import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.Returns;

import java.math.BigDecimal;
import java.util.List;

/**
 * Dao - 退货单
 * 
 * @author JQB Team
 * @version 3.0
 */
public interface ReturnsDao extends BaseDao<Returns, Long> {
    public List<Returns> findByOrderId(Long orderId);

    public Returns findByOrderIdAndItemId(Long orderId, String itemSn);

    public Page<Returns> findPage(Pageable pageable,  Member member);

	/**
	 * 根据商户计算申请退货成本价总价 状态（未确认  已确认  已完成 )
	
	 * 
	 * @param commercialId
	 *            商户id
	 * @return 申请退货成本总价，若不存在则返回null
	 */
	public BigDecimal sumAmountByCommercial(Long commercialId);
}
