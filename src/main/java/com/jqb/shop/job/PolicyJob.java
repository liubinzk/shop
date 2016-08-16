package com.jqb.shop.job;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jqb.shop.entity.policy.Policy;
import com.jqb.shop.service.PolicyService;

/**
 * Job - 保险定时任务
 * 
 * @author JQB Team
 * @version 3.0
 */
@Component("policyJob")
@Lazy(false)
public class PolicyJob {

    @Resource(name = "policyServiceImpl")
    private PolicyService policyServiceImpl;

	/**
	 * 释放过期订单库存
	 */
	@Scheduled(cron = "${job.policy_HttpSendForMS.cron}")
	public void HttpSendForMS() {
//		Policy policy=new Policy();
//		Date date=new Date();
//		policy.setCreateDate(date);
//		policy.setModifyDate(date);
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.DAY_OF_MONTH, 1);
//		policy.setBeginTime(calendar.getTime());
//		policy.setProductCode(policyServiceImpl.productCode_1);
//		policy.setPersonnelName("朱廷发");
//		policy.setSexCode(1);
//		policy.setCertificateNo("350821198309130834");
//		policy.setBirthday("1983-09-13");
//		policy.setMobileTelephone("18500136880");
//		policy.setEmail("zhu.ting.fa@163.com");
//		policyServiceImpl.save(policy);
		 List<Policy> policys=policyServiceImpl.findUnSend();
		 if( policys!=null){
			 for(Policy policy1:policys){
				 policyServiceImpl.createPolicy(policy1);
			 }
		 }
	}
}
