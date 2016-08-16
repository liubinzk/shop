/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao.impl;

import com.jqb.shop.dao.BrandDao;
import com.jqb.shop.entity.Brand;

import org.springframework.stereotype.Repository;

/**
 * Dao - 品牌
 * 
 * @author JQB Team
 * @version 3.0
 */
@Repository("brandDaoImpl")
public class BrandDaoImpl extends BaseDaoImpl<Brand, Long> implements BrandDao {

}
