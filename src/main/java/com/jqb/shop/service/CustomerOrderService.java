package com.jqb.shop.service;

import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.entity.CustomerOrder;
import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.Order;

/**
 * Created by liubin on 2016/4/7.
 */
public interface CustomerOrderService extends BaseService<CustomerOrder,Long> {

    /**
     * 查找订单分页
     *
     * @param orderStatus
     *            订单状态
     * @param paymentStatus
     *            支付状态
     * @param shippingStatus
     *            配送状态
     * @param hasExpired
     *            是否已过期
     * @param pageable
     *            分页信息
     * @return 商品分页
     */
    Page< CustomerOrder> findPage(Member member, CustomerOrder.OrderStatus orderStatus,  CustomerOrder.PaymentStatus paymentStatus,  CustomerOrder.ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable);


    public CustomerOrder findBySn(String sn);
}
