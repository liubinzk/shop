/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.service;

import java.awt.image.BufferedImage;

import com.jqb.shop.Setting.CaptchaType;

/**
 * Service - 验证码
 * 
 * @author JQB Team
 * @version 3.0
 */
public interface CaptchaService {

	/**
	 * 生成验证码图片
	 * 
	 * @param captchaId
	 *            验证ID
	 * @return 验证码图片
	 */
	BufferedImage buildImage(String captchaId);

	/**
	 * 验证码验证
	 * 
	 * @param captchaType
	 *            验证码类型
	 * @param captchaId
	 *            验证ID
	 * @param captcha
	 *            验证码(忽略大小写)
	 * @return 验证码验证是否通过
	 */
	boolean isValid(CaptchaType captchaType, String captchaId, String captcha);

}
