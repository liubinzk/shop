/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import com.jqb.shop.Filter;
import com.jqb.shop.Order;
import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.dao.ArticleDao;
import com.jqb.shop.entity.Article;
import com.jqb.shop.entity.ArticleCategory;
import com.jqb.shop.entity.Tag;
import com.jqb.shop.service.ArticleService;
import com.jqb.shop.service.StaticService;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Service - 文章
 * 
 * @author JQB Team
 * @version 3.0
 */
@Service("articleServiceImpl")
public class ArticleServiceImpl extends BaseServiceImpl<Article, Long> implements ArticleService, DisposableBean {

	/** 查看点击数时间 */
	private long viewHitsTime = System.currentTimeMillis();

	@Resource(name = "ehCacheManager")
	private CacheManager cacheManager;
	@Resource(name = "articleDaoImpl")
	private ArticleDao articleDao;
	@Resource(name = "staticServiceImpl")
	private StaticService staticService;

	@Resource(name = "articleDaoImpl")
	public void setBaseDao(ArticleDao articleDao) {
		super.setBaseDao(articleDao);
	}

	@Transactional(readOnly = true)
	public List<Article> findList(ArticleCategory articleCategory, List<Tag> tags, Integer count, List<Filter> filters, List<Order> orders) {
		return articleDao.findList(articleCategory, tags, count, filters, orders);
	}

	@Transactional(readOnly = true)
	@Cacheable("article")
	public List<Article> findList(ArticleCategory articleCategory, List<Tag> tags, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion) {
		return articleDao.findList(articleCategory, tags, count, filters, orders);
	}

	@Transactional(readOnly = true)
	public List<Article> findList(ArticleCategory articleCategory, Date beginDate, Date endDate, Integer first, Integer count) {
		return articleDao.findList(articleCategory, beginDate, endDate, first, count);
	}

	@Transactional(readOnly = true)
	public Page<Article> findPage(ArticleCategory articleCategory, List<Tag> tags, Pageable pageable) {
		return articleDao.findPage(articleCategory, tags, pageable);
	}

	public long viewHits(Long id) {
		Ehcache cache = cacheManager.getEhcache(Article.HITS_CACHE_NAME);
		Element element = cache.get(id);
		Long hits;
		if (element != null) {
			hits = (Long) element.getObjectValue();
		} else {
			Article article = articleDao.find(id);
			if (article == null) {
				return 0L;
			}
			hits = article.getHits();
		}
		hits++;
		cache.put(new Element(id, hits));
		long time = System.currentTimeMillis();
		if (time > viewHitsTime + Article.HITS_CACHE_INTERVAL) {
			viewHitsTime = time;
			updateHits();
			cache.removeAll();
		}
		return hits;
	}

	public void destroy() throws Exception {
		updateHits();
	}

	/**
	 * 更新点击数
	 */
	@SuppressWarnings("unchecked")
	private void updateHits() {
		Ehcache cache = cacheManager.getEhcache(Article.HITS_CACHE_NAME);
		List<Long> ids = cache.getKeys();
		for (Long id : ids) {
			Article article = articleDao.find(id);
			if (article != null) {
				Element element = cache.get(id);
				long hits = (Long) element.getObjectValue();
				article.setHits(hits);
				articleDao.merge(article);
			}
		}
	}

	@Override
	@Transactional
	@CacheEvict(value = { "article", "articleCategory" }, allEntries = true)
	public void save(Article article) {
		Assert.notNull(article);

		super.save(article);
		articleDao.flush();
		staticService.build(article);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "article", "articleCategory" }, allEntries = true)
	public Article update(Article article) {
		Assert.notNull(article);

		Article pArticle = super.update(article);
		articleDao.flush();
		staticService.build(pArticle);
		return pArticle;
	}

	@Override
	@Transactional
	@CacheEvict(value = { "article", "articleCategory" }, allEntries = true)
	public Article update(Article article, String... ignoreProperties) {
		return super.update(article, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "article", "articleCategory" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "article", "articleCategory" }, allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "article", "articleCategory" }, allEntries = true)
	public void delete(Article article) {
		if (article != null) {
			staticService.delete(article);
		}
		super.delete(article);
	}

}
