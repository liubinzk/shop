package com.jqb.shop.service;

import java.math.BigDecimal;

import com.jqb.shop.entity.SettleAccounts;

/**
 * Created by liubin on 2016/4/9.
 */

public interface SettleAccountsService extends BaseService<SettleAccounts, Long> {
	/**
	 * 根据商户计算成本价总价
	 * 
	 * @param commercialId
	 *            商户id
	 * @return 成本总价，若不存在则返回null
	 */
	public BigDecimal sumAmountByOrderCommercial(Long commercialId);
	/**
	 * 根据商户计算申请退货成本价总价
	 * 
	 * @param commercialId
	 *            商户id
	 * @return 申请退货成本总价，若不存在则返回null
	 */
	public BigDecimal sumAmountByReturnsCommercial(Long commercialId);
	/**
	 * 根据商户计算已经申请提现总价
	 * 
	 * @param commercialId
	 *            商户id
	 * @return 申请提现总价，若不存在则返回null
	 */
	public BigDecimal sumAmountByCommercial(Long commercialId);
	
	void audit(Long id);
}
