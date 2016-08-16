/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.job;

import javax.annotation.Resource;

import com.jqb.shop.service.StaticService;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Job - 静态化
 * 
 * @author JQB Team
 * @version 3.0
 */
@Component("staticJob")
@Lazy(false)
public class StaticJob {

	@Resource(name = "staticServiceImpl")
	private StaticService staticService;

	/**
	 * 生成静态
	 */
	@Scheduled(cron = "${job.static_build.cron}")
	public void build() {
		staticService.buildAll();
	}

}
