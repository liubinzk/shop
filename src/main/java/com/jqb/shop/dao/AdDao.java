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
import com.jqb.shop.entity.Ad;
import com.jqb.shop.entity.AdPosition;

/**
 * Dao - 广告
 * 
 * @author JQB Team
 * @version 3.0
 */
public interface AdDao extends BaseDao<Ad, Long> {

    public Page<Ad> findPage( long adId, Pageable pageable);
}
