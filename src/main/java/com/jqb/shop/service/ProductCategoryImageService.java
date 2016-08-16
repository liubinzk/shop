/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.service;

import com.jqb.shop.entity.ProductCategoryImage;

/**
 * Service - 商品图片
 * 
 * @author JQB Team
 * @version 3.0
 */
public interface ProductCategoryImageService {

	/**
	 * 生成商品图片
	 * 
	 * @param productCategoryImage
	 *            商品图片
	 */
	void build(ProductCategoryImage productCategoryImage);

}
