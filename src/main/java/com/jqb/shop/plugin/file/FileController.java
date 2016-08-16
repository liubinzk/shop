/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.plugin.file;

import javax.annotation.Resource;

import com.jqb.shop.controller.admin.BaseController;
import com.jqb.shop.entity.PluginConfig;
import com.jqb.shop.service.PluginConfigService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller - 本地文件存储
 * 
 * @author JQB Team
 * @version 3.0
 */
@Controller("adminPluginFileController")
@RequestMapping("/admin/storage_plugin/file")
public class FileController extends BaseController {

	@Resource(name = "filePlugin")
	private FilePlugin filePlugin;
	@Resource(name = "pluginConfigServiceImpl")
	private PluginConfigService pluginConfigService;

	/**
	 * 设置
	 */
	@RequestMapping(value = "/setting", method = RequestMethod.GET)
	public String setting(ModelMap model) {
		PluginConfig pluginConfig = filePlugin.getPluginConfig();
		model.addAttribute("pluginConfig", pluginConfig);
		return "/net/shopxx/plugin/file/setting";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(Integer order, RedirectAttributes redirectAttributes) {
		PluginConfig pluginConfig = filePlugin.getPluginConfig();
		pluginConfig.setIsEnabled(true);
		pluginConfig.setOrder(order);
		pluginConfigService.update(pluginConfig);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:/admin/storage_plugin/list.jhtml";
	}

}
