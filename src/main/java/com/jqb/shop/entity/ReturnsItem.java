/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.entity;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.math.BigDecimal;

/**
 * Entity - 退货项
 * 
 * @author JQB Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_returns_item")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_returns_item_sequence")
public class ReturnsItem extends BaseEntity {

	private static final long serialVersionUID = -4112374596087084162L;

	/** 商品编号 */
	private String sn;

	/** 商品名称 */
	private String name;

	/** 数量 */
	private Integer quantity;

	/** 退货单 */
	private Returns returns;

	/** 商品价格 */
	private BigDecimal price;

	/** 成本价 */
	private BigDecimal cost;

	/**
	 * 获取商品编号
	 * 
	 * @return 商品编号
	 */
	@NotEmpty
	@Column(nullable = false, updatable = false)
	public String getSn() {
		return sn;
	}

	/**
	 * 设置商品编号
	 * 
	 * @param sn
	 *            商品编号
	 */
	public void setSn(String sn) {
		this.sn = sn;
	}

	/**
	 * 获取商品名称
	 * 
	 * @return 商品名称
	 */
	@NotEmpty
	@Column(nullable = false, updatable = false)
	public String getName() {
		return name;
	}

	/**
	 * 设置商品名称
	 * 
	 * @param name
	 *            商品名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取数量
	 * 
	 * @return 数量
	 */
	@NotNull
	@Min(1)
	@Column(nullable = false, updatable = false)
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
	 * 获取退货单
	 * 
	 * @return 退货单
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = false)
	public Returns getReturns() {
		return returns;
	}

	/**
	 * 设置退货单
	 * 
	 * @param returns
	 *            退货单
	 */
	public void setReturns(Returns returns) {
		this.returns = returns;
	}

	/**
	 * 获取成本价
	 *
	 * @return 成本价
	 */
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(precision = 21, scale = 6)
	public BigDecimal getCost() {
		return cost;
	}

	/**
	 * 设置成本价
	 *
	 * @param cost
	 *            成本价
	 */
	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	/**
	 * 获取商品价格
	 *
	 * @return 商品价格
	 */
	@JsonProperty
	@NotNull
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * 设置商品价格
	 *
	 * @param price
	 *            商品价格
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	/**
	 * 获取小计
	 *
	 * @return 小计
	 */
	@Transient
	public BigDecimal getSubtotal() {
		if (getPrice() != null && getQuantity() != null) {
			return getPrice().multiply(new BigDecimal(getQuantity()));
		} else {
			return new BigDecimal(0);
		}
	}


}
