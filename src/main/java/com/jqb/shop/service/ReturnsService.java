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
import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.Returns;

import java.util.List;

/**
 * Service - 退货单
 * 
 * @author JQB Team
 * @version 3.0
 */
public interface ReturnsService extends BaseService<Returns, Long> {

    public List<Returns> findByOrderId(Long orderId);
    public Returns findByOrderIdAndItemId(Long orderId, String itemSn);
    public Page<Returns> findPage(Pageable pageable,  Member member);
}
