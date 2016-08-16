/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.entity;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity - 商户
 * 
 * @author JQB Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_commercial")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_commercial_sequence")
public class Commercial extends BaseEntity {

	private static final long serialVersionUID = 10595703086045998L;

	/** 名称 */
	private String name;

	/** 名称 */
	private String fullName;

	/** 网址 */
	private String url;

	/** 工商号 */
	private String code;

	/**  描述*/
	private String introduction;

	/** 银行账号 */
	private String account;

	/** 开户行 */
	private String depositBank;


	/**
	 * 获取名称
	 * 
	 * @return 名称
	 */
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * 
	 * @param name
	 *            名称
	 */
	public void setName(String name) {
		this.name = name;
	}



	/**
	 * 获取网址
	 * 
	 * @return 网址
	 */
	@Length(max = 200)
	public String getUrl() {
		return url;
	}

	/**
	 * 设置网址
	 * 
	 * @param url
	 *            网址
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 获取代码
	 * 
	 * @return 代码
	 */
	@Length(max = 200)
	public String getCode() {
		return code;
	}

	/**
	 * 设置代码
	 * 
	 * @param code
	 *            代码
	 */
	public void setCode(String code) {
		this.code = code;
	}

	@Column(name="introduction")
	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	@Column(name="full_name")
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@Column(name="account")
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Column(name="deposit_bank")
	public String getDepositBank() {
		return depositBank;
	}

	public void setDepositBank(String depositBank) {
		this.depositBank = depositBank;
	}
}
