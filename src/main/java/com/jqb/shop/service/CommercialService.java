package com.jqb.shop.service;

import com.jqb.shop.entity.Commercial;

/**
 * Created by liubin on 2016/4/7.
 */
public interface CommercialService extends BaseService<Commercial, Long> {

	void deleteAndCheck(Long id);
}