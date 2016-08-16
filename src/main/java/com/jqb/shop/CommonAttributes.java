/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop;

/**
 * 公共参数
 * 
 * @author JQB Team
 * @version 3.0
 */
public final class CommonAttributes {

	/** 日期格式配比 */
	public static final String[] DATE_PATTERNS = new String[] { "yyyy", "yyyy-MM", "yyyyMM", "yyyy/MM", "yyyy-MM-dd", "yyyyMMdd", "yyyy/MM/dd", "yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss", "yyyy/MM/dd HH:mm:ss" };

	/** jqbshop.xml文件路径 */
	public static final String JQBSHOP_XML_PATH = "/jqbshop.xml";

	/** jqbshop.properties文件路径 */
	public static final String JQBSHOP_PROPERTIES_PATH = "/jqbshop.properties";

	/**
	 * 不可实例化
	 */
	private CommonAttributes() {


	}

}
