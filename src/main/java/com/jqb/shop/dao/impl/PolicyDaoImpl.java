package com.jqb.shop.dao.impl;

import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;

import com.jqb.shop.dao.PolicyDao;
import com.jqb.shop.entity.policy.Policy;

/**
 * Created by liubin on 2016/1/13.
 */
@Repository("policyDaoImpl")
public class PolicyDaoImpl extends BaseDaoImpl<Policy, Long> implements PolicyDao {

    public Policy findByNo(String policyNo) {
        try {
            String jpql = "select policy from Policy policy where lower(policy.policyNo) = lower(:policyNo)";
            return entityManager.createQuery(jpql, Policy.class).setFlushMode(FlushModeType.COMMIT).setParameter("policyNo", policyNo).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public List<Policy> findByMemberId(Long uid, String productCode) {
        try {
            String jpql = "select policy from Policy policy where policy.uid = :uid and policy.productCode = :productCode";
            return  entityManager.createQuery(jpql, Policy.class).setFlushMode(FlushModeType.COMMIT).setParameter("uid", uid).setParameter("productCode", productCode).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
    public List<Policy> findUnSend() {
        try {
            String jpql = "select policy from Policy policy where policy.state = 0";
            //每次获取1000条数据
            return  entityManager.createQuery(jpql, Policy.class).setFlushMode(FlushModeType.COMMIT).setFirstResult(0).setMaxResults(1000).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
