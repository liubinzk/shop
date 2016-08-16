/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.jqb.shop.Filter;
import com.jqb.shop.Order;
import com.jqb.shop.dao.BrandDao;
import com.jqb.shop.entity.Brand;
import com.jqb.shop.service.BrandService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 品牌
 * 
 * @author JQB Team
 * @version 3.0
 */
@Service("brandServiceImpl")
public class BrandServiceImpl extends BaseServiceImpl<Brand, Long> implements BrandService {

	@Resource(name = "brandDaoImpl")
	private BrandDao brandDao;

	@Resource(name = "brandDaoImpl")
	public void setBaseDao(BrandDao brandDao) {
		super.setBaseDao(brandDao);
	}

	@Transactional(readOnly = true)
	@Cacheable("brand")
	public List<Brand> findList(Integer count, List<Filter> filters, List<Order> orders, String cacheRegion) {
		return brandDao.findList(null, count, filters, orders);
	}

	@Override
	@Transactional
	@CacheEvict(value = "brand", allEntries = true)
	public void save(Brand brand) {
		super.save(brand);
	}

	@Override
	@Transactional
	@CacheEvict(value = "brand", allEntries = true)
	public Brand update(Brand brand) {
		return super.update(brand);
	}

	@Override
	@Transactional
	@CacheEvict(value = "brand", allEntries = true)
	public Brand update(Brand brand, String... ignoreProperties) {
		return super.update(brand, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = "brand", allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = "brand", allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = "brand", allEntries = true)
	public void delete(Brand brand) {
		super.delete(brand);
	}

}
