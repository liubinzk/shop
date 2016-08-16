/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.restful.entity;

import com.jqb.shop.entity.BaseEntity;
import com.jqb.shop.entity.Product;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Entity - 购物车项
 * 
 * @author JQB Team
 * @version 3.0
 */
public class MobileCartItem extends BaseEntity {

	private static final long serialVersionUID = 2979296789363163144L;

	/** 最大数量 */
	public static final Integer MAX_QUANTITY = 10000;

	/** 数量 */
	private Integer quantity;

	/** 商品 */
	private Product product;


	/** 临时商品价格 */
	private BigDecimal tempPrice;

	/** 临时赠送积分 */
	private Long tempPoint;

	private BigDecimal price;

	/**
	 * 获取数量
	 * 
	 * @return 数量
	 */
	@Column(nullable = false)
	public Integer getQuantity() {
		return quantity;
	}

	/**
	 * 设置数量
	 * 
	 * @param quantity
	 *            数量
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	/**
	 * 获取商品
	 * 
	 * @return 商品
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = false)
	public Product getProduct() {
		return product;
	}

	/**
	 * 设置商品
	 * 
	 * @param product
	 *            商品
	 */
	public void setProduct(Product product) {
		this.product = product;
	}



	/**
	 * 获取临时商品价格
	 * 
	 * @return 临时商品价格
	 */
	@Transient
	public BigDecimal getTempPrice() {
		if (tempPrice == null) {
			return getSubtotal();
		}
		return tempPrice;
	}

	/**
	 * 设置临时商品价格
	 * 
	 * @param tempPrice
	 *            临时商品价格
	 */
	@Transient
	public void setTempPrice(BigDecimal tempPrice) {
		this.tempPrice = tempPrice;
	}

	/**
	 * 获取临时赠送积分
	 * 
	 * @return 临时赠送积分
	 */
	@Transient
	public Long getTempPoint() {
		if (tempPoint == null) {
			return getPoint();
		}
		return tempPoint;
	}

	/**
	 * 设置临时赠送积分
	 * 
	 * @param tempPoint
	 *            临时赠送积分
	 */
	@Transient
	public void setTempPoint(Long tempPoint) {
		this.tempPoint = tempPoint;
	}

	/**
	 * 获取赠送积分
	 * 
	 * @return 赠送积分
	 */
	@Transient
	public long getPoint() {
		if (getProduct() != null && getProduct().getPoint() != null && getQuantity() != null) {
			return getProduct().getPoint() * getQuantity();
		} else {
			return 0L;
		}
	}

	/**
	 * 获取商品重量
	 * 
	 * @return 商品重量
	 */
	@Transient
	public int getWeight() {
		if (getProduct() != null && getProduct().getWeight() != null && getQuantity() != null) {
			return getProduct().getWeight() * getQuantity();
		} else {
			return 0;
		}
	}

	/**
	 * 获取价格
	 * 
	 * @return 价格
	 */
	@Transient
	public BigDecimal getPrice() { return price;}

	/**
	 * 获取小计
	 * 
	 * @return 小计
	 */
	@Transient
	public BigDecimal getSubtotal() {
		if (getQuantity() != null) {
			return getPrice().multiply(new BigDecimal(getQuantity()));
		} else {
			return new BigDecimal(0);
		}
	}

	/**
	 * 获取是否库存不足
	 * 
	 * @return 是否库存不足
	 */
	@Transient
	public boolean getIsLowStock() {
		if (getQuantity() != null && getProduct() != null && getProduct().getStock() != null && getQuantity() > getProduct().getAvailableStock()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 增加商品数量
	 * 
	 * @param quantity
	 *            数量
	 */
	@Transient
	public void add(int quantity) {
		if (quantity > 0) {
			if (getQuantity() != null) {
				setQuantity(getQuantity() + quantity);
			} else {
				setQuantity(quantity);
			}
		}
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}
