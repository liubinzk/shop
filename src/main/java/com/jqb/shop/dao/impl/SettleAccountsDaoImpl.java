package com.jqb.shop.dao.impl;

import java.math.BigDecimal;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import com.jqb.shop.dao.SettleAccountsDao;
import com.jqb.shop.entity.SettleAccounts;

import org.springframework.stereotype.Repository;

/**
 * Created by liubin on 2016/4/9.
 */
@Repository("settleAccountsDaoImpl")
public class SettleAccountsDaoImpl extends BaseDaoImpl<SettleAccounts, Long> implements SettleAccountsDao {

	public BigDecimal sumAmountByCommercial(Long commercialId) {
		BigDecimal result=BigDecimal.ZERO;
		if(commercialId==null){
			return result;
		}

		String jpql = "select sum(settleAccounts.amount) from SettleAccounts settleAccounts where settleAccounts.commercial.id =:commercialId";
		try {
			result= entityManager.createQuery(jpql,BigDecimal.class).setFlushMode(FlushModeType.COMMIT).setParameter("commercialId", commercialId).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
		if(	result==null){
			result=BigDecimal.ZERO;
		}
		return result;
	}

}
