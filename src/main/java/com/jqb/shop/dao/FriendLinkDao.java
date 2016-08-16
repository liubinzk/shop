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

import com.jqb.shop.entity.FriendLink;
import com.jqb.shop.entity.FriendLink.Type;

/**
 * Dao - 友情链接
 * 
 * @author JQB Team
 * @version 3.0
 */
public interface FriendLinkDao extends BaseDao<FriendLink, Long> {

	/**
	 * 查找友情链接
	 * 
	 * @param type
	 *            类型
	 * @return 友情链接
	 */
	List<FriendLink> findList(Type type);

}
