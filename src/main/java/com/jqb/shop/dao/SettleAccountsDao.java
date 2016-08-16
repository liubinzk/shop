package com.jqb.shop.dao;

import java.math.BigDecimal;

import com.jqb.shop.entity.SettleAccounts;

/**
 * Created by liubin on 2016/4/9.
 */
public interface SettleAccountsDao extends BaseDao<SettleAccounts, Long>{
	/**
	 * 根据商户计算已经申请提现总价
	 * 
	 * @param commercialId
	 *            商户id
	 * @return 申请提现总价，若不存在则返回null
	 */
	public BigDecimal sumAmountByCommercial(Long commercialId);

}
