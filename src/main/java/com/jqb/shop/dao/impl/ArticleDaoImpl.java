/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import com.jqb.shop.Filter;
import com.jqb.shop.Order;
import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.dao.ArticleDao;
import com.jqb.shop.entity.Article;
import com.jqb.shop.entity.ArticleCategory;
import com.jqb.shop.entity.Tag;

import org.springframework.stereotype.Repository;

/**
 * Dao - 文章
 * 
 * @author JQB Team
 * @version 3.0
 */
@Repository("articleDaoImpl")
public class ArticleDaoImpl extends BaseDaoImpl<Article, Long> implements ArticleDao {

	public List<Article> findList(ArticleCategory articleCategory, List<Tag> tags, Integer count, List<Filter> filters, List<Order> orders) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Article> criteriaQuery = criteriaBuilder.createQuery(Article.class);
		Root<Article> root = criteriaQuery.from(Article.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isPublication"), true));
		if (articleCategory != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.equal(root.get("articleCategory"), articleCategory), criteriaBuilder.like(root.get("articleCategory").<String> get("treePath"), "%" + ArticleCategory.TREE_PATH_SEPARATOR + articleCategory.getId() + ArticleCategory.TREE_PATH_SEPARATOR + "%")));
		}
		if (tags != null && !tags.isEmpty()) {
			Subquery<Article> subquery = criteriaQuery.subquery(Article.class);
			Root<Article> subqueryRoot = subquery.from(Article.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("tags").in(tags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		criteriaQuery.where(restrictions);
		criteriaQuery.orderBy(criteriaBuilder.desc(root.get("isTop")));
		return super.findList(criteriaQuery, null, count, filters, orders);
	}

	public List<Article> findList(ArticleCategory articleCategory, Date beginDate, Date endDate, Integer first, Integer count) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Article> criteriaQuery = criteriaBuilder.createQuery(Article.class);
		Root<Article> root = criteriaQuery.from(Article.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isPublication"), true));
		if (articleCategory != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.equal(root.get("articleCategory"), articleCategory), criteriaBuilder.like(root.get("articleCategory").<String> get("treePath"), "%" + ArticleCategory.TREE_PATH_SEPARATOR + articleCategory.getId() + ArticleCategory.TREE_PATH_SEPARATOR + "%")));
		}
		if (beginDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), beginDate));
		}
		if (endDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(root.<Date> get("createDate"), endDate));
		}
		criteriaQuery.where(restrictions);
		criteriaQuery.orderBy(criteriaBuilder.desc(root.get("isTop")));
		return super.findList(criteriaQuery, first, count, null, null);
	}

	public Page<Article> findPage(ArticleCategory articleCategory, List<Tag> tags, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Article> criteriaQuery = criteriaBuilder.createQuery(Article.class);
		Root<Article> root = criteriaQuery.from(Article.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isPublication"), true));
		if (articleCategory != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.equal(root.get("articleCategory"), articleCategory), criteriaBuilder.like(root.get("articleCategory").<String> get("treePath"), "%" + ArticleCategory.TREE_PATH_SEPARATOR + articleCategory.getId() + ArticleCategory.TREE_PATH_SEPARATOR + "%")));
		}
		if (tags != null && !tags.isEmpty()) {
			Subquery<Article> subquery = criteriaQuery.subquery(Article.class);
			Root<Article> subqueryRoot = subquery.from(Article.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("tags").in(tags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		criteriaQuery.where(restrictions);
		criteriaQuery.orderBy(criteriaBuilder.desc(root.get("isTop")));
		return super.findPage(criteriaQuery, pageable);
	}

}
