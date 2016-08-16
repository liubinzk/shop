/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.job;

import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.entity.Order;
import com.jqb.shop.service.OrderService;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Job - 定时设置
 * 
 * @author JQB Team
 * @version 3.0
 */
@Component("orderStatusJob")
@Lazy(false)
public class OrderStatusJob {

	@Resource(name = "orderServiceImpl")
	private OrderService orderService;

	/** logger */
	private static final Logger logger = Logger.getLogger(OrderStatusJob.class.getName());

	/**
	 * 释放过期订单库存
	 */
	@Scheduled(cron = "${job.order_status.cron}")
	public void scheduleOrderStatus() {
		int pageNumber = 0,pageSize=100;
		Pageable pageable = new Pageable(pageNumber, pageSize);
		Page<Order> orderPage = orderService.findPage(pageable);
		this.changeOrderStatus(orderPage);
		if(orderPage.getTotalPages() > 1){
			for (pageNumber =1; pageNumber < orderPage.getTotalPages(); pageNumber++ ) {
				Page<Order> nextOrderPage = orderService.findPage(pageable);
				changeOrderStatus(nextOrderPage);
			}
		}
	}

	private void changeOrderStatus(Page<Order> orderPage){
		for(Order order : orderPage.getContent()){
			//由于没有物流信息确认，发货后七天设置为已完成状态
			if(order.getOrderStatus() != Order.OrderStatus.completed && order.getOrderStatus() != Order.OrderStatus.deleted &&
					order.getOrderStatus() != Order.OrderStatus.cancelled){
					if( order.getShippingStatus() == Order.ShippingStatus.shipped && (new Date()).getTime() - order.getCompletedDate().getTime() > 7 * 24 * 60 * 60 * 1000){
						order.setOrderStatus( Order.OrderStatus.completed );
						order.setCompletedDate( new Date() );
						orderService.update(order);
						logger.info("Update order " + order.getSn() + " status to complete.");
					}
			}
		}
	}

}
