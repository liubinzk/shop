/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.dao.impl;

import com.jqb.shop.dao.RoleDao;
import com.jqb.shop.entity.Role;

import org.springframework.stereotype.Repository;

/**
 * Dao - 角色
 * 
 * @author JQB Team
 * @version 3.0
 */
@Repository("roleDaoImpl")
public class RoleDaoImpl extends BaseDaoImpl<Role, Long> implements RoleDao {

}
