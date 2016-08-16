package com.jqb.shop.restful.entity;

import com.jqb.shop.entity.AdminReceiver;
import com.jqb.shop.entity.Product;
import com.jqb.shop.entity.Returns;
import com.jqb.shop.entity.ReturnsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 2016/2/24.
 */
public class RestfulReturns {

    private Returns returns;
    private List<Product> products =  new ArrayList<Product>();
    private AdminReceiver receiver = new AdminReceiver();



    public Returns getReturns() {
        return returns;
    }
    /** ID */
    private Long orderId;

    public void setReturns(Returns returns) {
        this.returns = returns;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public AdminReceiver getReceiver() {
        return receiver;
    }

    public void setReceiver(AdminReceiver receiver) {
        this.receiver = receiver;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

}
