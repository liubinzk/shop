package com.jqb.shop.restful.entity;

import com.jqb.shop.entity.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 2016/2/24.
 */
public class RestPromotion {

    private Ad ad = null;
    private List<Product> products =  new ArrayList<Product>();

    public Ad getAd() {
        return ad;
    }

    public void setAd(Ad ad) {
        this.ad = ad;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
