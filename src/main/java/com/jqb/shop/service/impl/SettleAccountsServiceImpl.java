package com.jqb.shop.service.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jqb.shop.dao.OrderDao;
import com.jqb.shop.dao.ReturnsDao;
import com.jqb.shop.dao.SettleAccountsDao;
import com.jqb.shop.dao.SnDao;
import com.jqb.shop.entity.SettleAccounts;
import com.jqb.shop.entity.SettleAccounts.Status;
import com.jqb.shop.entity.Sn.Type;
import com.jqb.shop.service.SettleAccountsService;

/**
 * Created by liubin on 2016/4/9.
 */
@Service("settleAccountsServiceImpl")
public class SettleAccountsServiceImpl extends BaseServiceImpl<SettleAccounts,Long> implements SettleAccountsService {
	   @Resource(name = "settleAccountsDaoImpl")
	    private SettleAccountsDao settleAccountsDao;
	    
		@Resource(name = "snDaoImpl")
		private SnDao snDao;
		
		@Resource(name = "orderDaoImpl")
		private OrderDao orderDao;
		
		@Resource(name = "returnsDaoImpl")
		private ReturnsDao returnsDao;
		
	    @Resource(name = "settleAccountsDaoImpl")
	    public void setBaseDao(SettleAccountsDao settleAccountsDao) {
	        super.setBaseDao(settleAccountsDao);
	    }
		@Transactional
		public void save(SettleAccounts entity) {
			entity.setSn(snDao.generate(Type.settleAccounts));
			super.save(entity);
		}
		public BigDecimal sumAmountByOrderCommercial(Long commercialId) {
			// TODO Auto-generated method stub
			return orderDao.sumAmountByCommercial(commercialId);
		}
		public BigDecimal sumAmountByReturnsCommercial(Long commercialId) {
			// TODO Auto-generated method stub
			return returnsDao.sumAmountByCommercial(commercialId);
		}
		public void audit(Long id) {
			SettleAccounts settleAccounts=super.find(id);
			settleAccounts.setPaymentDate(new Date());//审核时间=支付时间
			settleAccounts.setStatus(Status.success);//支付成功状态
			super.update(settleAccounts);
		}
		public BigDecimal sumAmountByCommercial(Long commercialId) {
			// TODO Auto-generated method stub
			return settleAccountsDao.sumAmountByCommercial(commercialId);
		}
}
