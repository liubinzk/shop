package com.jqb.shop.service.impl;

import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.dao.CustomerOrderDao;
import com.jqb.shop.entity.CustomerOrder;
import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.Order;
import com.jqb.shop.service.CustomerOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by liubin on 2016/4/7.
 */
@Service("customerOrderServiceImpl")
public class CustomerOrderServiceImpl extends BaseServiceImpl<CustomerOrder, Long> implements CustomerOrderService {

    @Resource(name="customerOrderDaoImpl")
    private CustomerOrderDao customerOrderDao;

    @Resource(name="customerOrderDaoImpl")
    public void setBaseDao(CustomerOrderDao customerOrderDao){
        super.setBaseDao(customerOrderDao);
    }
    @Transactional(readOnly = true)
    public Page<CustomerOrder> findPage(Member member,CustomerOrder.OrderStatus orderStatus, CustomerOrder.PaymentStatus paymentStatus, CustomerOrder.ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable) {
        return customerOrderDao.findPage(member,orderStatus, paymentStatus, shippingStatus, hasExpired, pageable);
    }

    @Transactional(readOnly = true)
    public CustomerOrder findBySn(String sn) {
        return customerOrderDao.findBySn(sn);
    }
}
