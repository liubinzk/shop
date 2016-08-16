/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.template.directive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.jqb.shop.Filter;
import com.jqb.shop.Order;
import com.jqb.shop.entity.Brand;
import com.jqb.shop.entity.Consultation;
import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.Product;
import com.jqb.shop.service.ConsultationService;
import com.jqb.shop.service.MemberService;
import com.jqb.shop.service.ProductService;
import com.jqb.shop.util.FreemarkerUtils;

import org.springframework.stereotype.Component;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 咨询
 * 
 * @author JQB Team
 * @version 3.0
 */
@Component("consultationListDirective")
public class ConsultationListDirective extends BaseDirective {

	/** "会员ID"参数名称 */
	private static final String MEMBER_ID_PARAMETER_NAME = "memberId";

	/** "商品ID"参数名称 */
	private static final String PRODUCT_ID_PARAMETER_NAME = "productId";

	/** 变量名称 */
	private static final String VARIABLE_NAME = "consultations";

	@Resource(name = "consultationServiceImpl")
	private ConsultationService consultationService;
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		Long memberId = FreemarkerUtils.getParameter(MEMBER_ID_PARAMETER_NAME, Long.class, params);
		Long productId = FreemarkerUtils.getParameter(PRODUCT_ID_PARAMETER_NAME, Long.class, params);

		Member member = memberService.find(memberId);
		Product product = productService.find(productId);

		List<Consultation> consultations;
		boolean useCache = useCache(env, params);
		String cacheRegion = getCacheRegion(env, params);
		Integer count = getCount(params);
		List<Filter> filters = getFilters(params, Brand.class);
		List<Order> orders = getOrders(params);
		if ((memberId != null && member == null) || (productId != null && product == null)) {
			consultations = new ArrayList<Consultation>();
		} else {
			if (useCache) {
				consultations = consultationService.findList(member, product, true, count, filters, orders, cacheRegion);
			} else {
				consultations = consultationService.findList(member, product, true, count, filters, orders);
			}
		}
		setLocalVariable(VARIABLE_NAME, consultations, env, body);
	}

}
