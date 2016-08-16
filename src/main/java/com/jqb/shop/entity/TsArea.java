/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity - 地区
 * 
 * @author JQB Team
 * @version 3.0
 */
@Entity
@Table(name = "ts_area")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "ts_area_sequence")
public class TsArea  {

	/** ID */
	/**
	 * 获取ID
	 *
	 * @return ID
	 */
	@JsonProperty
	@DocumentId
	@Id
	// MySQL/SQLServer: @GeneratedValue(strategy = GenerationType.AUTO)
	// Oracle: @GeneratedValue(strategy = GenerationType.AUTO, generator = "sequenceGenerator")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sequenceGenerator")
	private Long id;

	@Column( length = 11)
	private Long pid;

	@Column
	private String title;
	@Column
	private int sort;
	@Column
	private int jqbId;
	@Column
	private int orders;
	@Column
	private String ename;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public int getJqbId() {
		return jqbId;
	}

	public void setJqbId(int jqbId) {
		this.jqbId = jqbId;
	}

	public int getOrders() {
		return orders;
	}

	public void setOrders(int orders) {
		this.orders = orders;
	}

	public String getEname() {
		return ename;
	}

	public void setEname(String ename) {
		this.ename = ename;
	}
}
