package com.jqb.shop.service;

import java.util.List;

import com.jqb.shop.entity.policy.Policy;

/**
 * 保险
 * Created by liubin on 2016/1/13.
 */
public interface PolicyService  extends BaseService<Policy, Long> {
	public static final String productCode_1="25751";//目前赠送就第一种产品
	public static final String productCode_2="25752";
	public static final String productCode_3="25753";
	public static final String productCode_4="25754";
    public Policy findByNo(String policyNo);
	/*
	 * 按用户id，产品号查询保险
	 */
	public  List<Policy> findByMemberId(Long memberId, String productCode);

	/*
	 * 创建保险
	 */
	public boolean createPolicy(Policy policy);

	/*
	 * 注销保险
	 */
	public boolean cancelPolicy(Policy policy);
	
	public List<Policy> findUnSend();

}
