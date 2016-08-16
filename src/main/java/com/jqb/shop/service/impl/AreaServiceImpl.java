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

import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.dao.AreaDao;
import com.jqb.shop.dao.TsAreaDao;
import com.jqb.shop.entity.Area;
import com.jqb.shop.entity.TsArea;
import com.jqb.shop.service.AreaService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 地区
 * 
 * @author JQB Team
 * @version 3.0
 */
@Service("areaServiceImpl")
public class AreaServiceImpl extends BaseServiceImpl<Area, Long> implements AreaService {

	@Resource(name = "areaDaoImpl")
	private AreaDao areaDao;

	@Resource(name = "tsAreaDaoImpl")
	private TsAreaDao tsAreaDao;

	@Resource(name = "areaDaoImpl")
	public void setBaseDao(AreaDao areaDao) {
		super.setBaseDao(areaDao);
	}

	@Transactional(readOnly = true)
	public List<Area> findRoots() {
		return areaDao.findRoots(null);
	}

	@Transactional(readOnly = true)
	public List<Area> findRoots(Integer count) {
		return areaDao.findRoots(count);
	}

	public List<TsArea> findTsAreaRoots() {
		return tsAreaDao.findRoots(null);
	}

	@Override
	@Transactional
	@CacheEvict(value = "area", allEntries = true)
	public void save(Area area) {
		super.save(area);
	}

	@Override
	@Transactional
	@CacheEvict(value = "area", allEntries = true)
	public Area update(Area area) {
		return super.update(area);
	}

	@Override
	@Transactional
	@CacheEvict(value = "area", allEntries = true)
	public Area update(Area area, String... ignoreProperties) {
		return super.update(area, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = "area", allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = "area", allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = "area", allEntries = true)
	public void delete(Area area) {
		super.delete(area);
	}
	@Transactional(readOnly = true)
	public Page<Area> findLvl2Page( Pageable pageable) {
		return areaDao.findLvl2Page( pageable);
	}

}
