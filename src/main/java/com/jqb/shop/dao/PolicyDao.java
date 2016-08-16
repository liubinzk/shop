package com.jqb.shop.dao;

import java.util.List;

import com.jqb.shop.entity.policy.Policy;

/**
 * Created by liubin on 2016/1/13.
 */
public interface PolicyDao extends BaseDao<Policy, Long> {

    /**
     * 根据policyNo查找保单
     *
     * @param policyNo
     *            用户名(忽略大小写)
     * @return 管理员，若不存在则返回null
     */
    Policy findByNo(String policyNo);
    
    /**
     * 根据用户id，产品类型查找保单
     * @return 管理员，若不存在则返回null
     */
    public List<Policy> findByMemberId(Long uid, String productCode) ;
    
    public List<Policy> findUnSend();
}
