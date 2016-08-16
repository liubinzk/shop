package com.jqb.shop.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.jqb.shop.Filter;
import com.jqb.shop.Filter.Operator;
import com.jqb.shop.dao.AdminDao;
import com.jqb.shop.dao.CommercialDao;
import com.jqb.shop.dao.OrderDao;
import com.jqb.shop.entity.Admin;
import com.jqb.shop.entity.Commercial;
import com.jqb.shop.entity.Order;
import com.jqb.shop.service.CommercialService;

/**
 * Created by liubin on 2016/4/7.
 */
@Repository("commercialServiceImpl")
public class CommercialServiceImpl extends BaseServiceImpl<Commercial, Long> implements CommercialService {

	
    @Resource(name = "commercialDaoImpl")
    private CommercialDao commercialDao;
    
	@Resource(name = "orderDaoImpl")
	private OrderDao orderDao;
    
	@Resource(name = "adminDaoImpl")
	private AdminDao adminDao;
	
    @Resource(name = "commercialDaoImpl")
    public void setBaseDao(CommercialDao commercialDao) {
        super.setBaseDao(commercialDao);
    }

	public void deleteAndCheck(Long id) {
		
		
		 List<Filter> filters=new ArrayList<Filter>();
		 
		//校验管理员是否有订单
		 filters.clear();
		 filters.add(new Filter("commercial", Operator.eq, id));
		 List<Order> list1=orderDao.findList(0, 50, filters, null);
		 if(list1.size()>0){
			 Commercial commercial=super.find(id);
			 throw new RuntimeException("当前'"+commercial.getName()+"'商户,已经有关联订单，无法删除");
		 }
		//校验管理员是否有设置当前商户
		 filters.clear();
		 filters.add(new Filter("commercial", Operator.eq, id));
		 List<Admin> list=adminDao.findList(0, 50, filters, null);
		 if(list.size()>0){
			 StringBuilder sb=new StringBuilder();
			 for(Admin sdmin:list){
				 sb.append(sdmin.getUsername()+",");
			 }
			 Commercial commercial=super.find(id);
			 throw new RuntimeException("管理员{"+sb+"}设置了当'"+commercial.getName()+"'商户,请先删除关联");
		 }

		super.delete(id);
	}
}
