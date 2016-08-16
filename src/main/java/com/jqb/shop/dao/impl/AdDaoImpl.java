/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao.impl;

import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.dao.AdDao;
import com.jqb.shop.entity.Ad;

import com.jqb.shop.entity.AdPosition;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;

/**
 * Dao - 广告
 * 
 * @author JQB Team
 * @version 3.0
 */
@Repository("adDaoImpl")
public class AdDaoImpl extends BaseDaoImpl<Ad, Long> implements AdDao {

    @PersistenceContext
    protected EntityManager entityManager;


    @Transactional(readOnly = true)
    public Page<Ad> findPage( long adId, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Ad> criteriaQuery = criteriaBuilder.createQuery(Ad.class);
        Root<Ad> root = criteriaQuery.from(Ad.class);
        criteriaQuery.select(root);
        //criteriaBuilder.equal(root.get("isMarketable"), isMarketable)
        criteriaQuery.where(criteriaBuilder.equal(root.get("adPosition").get("id"), adId));
        return super.findPage(criteriaQuery, pageable);
    }
}
