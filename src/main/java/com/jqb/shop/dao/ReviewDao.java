/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao;

import java.util.List;

import com.jqb.shop.Filter;
import com.jqb.shop.Order;
import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.Product;
import com.jqb.shop.entity.Review;
import com.jqb.shop.entity.Review.Type;

/**
 * Dao - 评论
 * 
 * @author JQB Team
 * @version 3.0
 */
public interface ReviewDao extends BaseDao<Review, Long> {

	/**
	 * 查找评论
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param type
	 *            类型
	 * @param isShow
	 *            是否显示
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 评论
	 */
	List<Review> findList(Member member, Product product, Type type, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders);

	/**
	 * 查找评论分页
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param type
	 *            类型
	 * @param isShow
	 *            是否显示
	 * @param pageable
	 *            分页信息
	 * @return 评论分页
	 */
	Page<Review> findPage(Member member, Product product, Type type, Boolean isShow, Pageable pageable);

	/**
	 * 查找评论数量
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param type
	 *            类型
	 * @param isShow
	 *            是否显示
	 * @return 评论数量
	 */
	Long count(Member member, Product product, Type type, Boolean isShow);

	/**
	 * 判断会员是否已评论该商品
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @return 是否已评论该商品
	 */
	boolean isReviewed(Member member, Product product);

	/**
	 * 计算商品总评分
	 * 
	 * @param product
	 *            商品
	 * @return 商品总评分，仅计算显示评论
	 */
	long calculateTotalScore(Product product);

	/**
	 * 计算商品评分次数
	 * 
	 * @param product
	 *            商品
	 * @return 商品评分次数，仅计算显示评论
	 */
	long calculateScoreCount(Product product);

}
